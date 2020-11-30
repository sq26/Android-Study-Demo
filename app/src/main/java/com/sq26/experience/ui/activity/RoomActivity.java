package com.sq26.experience.ui.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sq26.experience.R;
import com.sq26.experience.adapter.RecyclerViewListAdapter;
import com.sq26.experience.adapter.ViewHolder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;

public class RoomActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<Word> wordList;
    private RecyclerViewListAdapter<Word> recyclerViewListAdapter;
    private WordViewModel wordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        wordViewModel =
                new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                        .get(WordViewModel.class);
        wordViewModel.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                if (wordList == null) {
                    wordList = words;
                    recyclerViewListAdapter = new RecyclerViewListAdapter<Word>(R.layout.item_recyclerview, wordList) {
                        @Override
                        protected void bindViewHolder(ViewHolder viewHolder, Word item, int position) {
                            viewHolder.setText(R.id.text, item.getWord());
                        }
                    };
                    recyclerView.setAdapter(recyclerViewListAdapter);
                } else {
                    recyclerViewListAdapter.setDate(words);
                    recyclerViewListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wordViewModel.insert(new Word(editText.getText().toString()));
                    }
                }).show();
    }

    //创建数据实体
    //@Entity:每个@Entity类代表一个SQLite表。注释您的类声明以表明它是一个实体。如果希望表名与类名不同，则可以指定表名。这将表命名为“ word_table”。
    @Entity(tableName = "word_table")
    static class Word {
        //自动生成唯一key
//        @PrimaryKey(autoGenerate = true)
//        @ColumnInfo
//        private int id;
        //@PrimaryKey:每个实体都需要一个主键。为了简单起见，每个单词都充当其自己的主键。
        //@ColumnInfo:如果希望与成员变量的名称不同，请在表中指定列的名称。
        @PrimaryKey
        @NonNull
        @ColumnInfo(name = "word")
        private String word;

        public Word(String word) {
            this.word = word;
        }

        public String getWord() {
            return word;
        }

    }

    //创建DAO
    //DAO必须是接口或抽象类。
    //@Dao:注解标识它作为一个ROOM DAO类
    @Dao
    interface WordDao {
        //声明一种插入一个单词的方法
        //@Insert:是一种特殊的DAO方法注释，无需提供任何SQL！

        /**
         * onConflict:各种Dao方法的冲突处理策略集,操作失败后的处理
         * ABORT:可中止事务。事务回滚。默认值
         * FAIL:使事务失败,不建议使用此常数。
         * IGNORE:忽视
         * REPLACE:可替换旧数据并继续进行事务
         * ROLLBACK:以回滚事务,不适用于Android当前的SQLite绑定。使用ABORT回滚事务。
         */
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insert(Word word);

        /**
         * 删除所有单词的方法。
         *
         * @Query:提供SQL语句 DELETE FROM word_table:删除word_table表
         */
        @Query("DELETE FROM word_table")
        void deleteAll();

        /**
         * 获取所有单词并使其返回的List方法
         * SELECT * from word_table ORDER BY word ASC:返回按升序排列的单词列表
         */
        @Query("SELECT * from word_table ORDER BY word ASC")
        LiveData<List<Word>> getAlphabetizedWords();
    }

    //Room的数据库类必须是abstract并且扩展RoomDatabase
    //@Database:标识此类是RoomDatabase
    //entities:要在数据库中创建的表的数组
    //version:数据库版本
    //exportSchema:是否将数据库导出到文件夹中,默认true,当room.schemaLocation(在app/build.gradle文件中设置)设置时,如果它被设置为true，
    // 数据库框架将被导出到指定文件夹。 设置false,仅使用数据库,
    @Database(entities = {Word.class}, version = 1, exportSchema = true)
    abstract static class WordRoomDatabase extends RoomDatabase {
        public abstract WordDao wordDao();

        //定义为单例模式
        private static volatile WordRoomDatabase INSTANCE;
        private static final int NUMBER_OF_THREADS = 4;
        //创建了一个ExecutorService带有固定线程池的,使用该池在后台线程上异步运行数据库操作。
        static final ExecutorService databaseWriteExecutor =
                Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        //返回单例
        static WordRoomDatabase getDatabase(Context context) {
            if (INSTANCE == null) {
                synchronized (WordRoomDatabase.class) {
                    //它将在首次访问数据库时使用Room的数据库构建器RoomDatabase在类的应用程序上下文中创建一个对象WordRoomDatabase并将其命名，
                    // 从而创建数据库"word_database"。
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), WordRoomDatabase.class, "word_database")
                            .build();
                }
            }
            //返回唯一实例
            return INSTANCE;
        }
    }

    static class WordRepository {
        private WordDao mWordDao;
        private LiveData<List<Word>> mAllWords;

        // 请注意，为了对WordRepository进行单元测试，必须删除Application依赖项。 这增加了复杂性和更多的代码，并且此示例与测试无关。
        // https://github.com/googlesamples
        WordRepository(Application application) {
            WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
            mWordDao = db.wordDao();

            mAllWords = mWordDao.getAlphabetizedWords();
        }

        //Room在单独的线程上执行所有查询。 观察到的LiveData将在数据更改时通知观察者。
        //返回LiveData单词列表；
        // 我们之所以可以这样做，是因为我们定义getAlphabetizedWords要使用LiveData返回的方法。
        // Room在单独的线程上执行所有查询。然后，LiveData当数据已更改时，观察者将在主线程上通知观察者。
        LiveData<List<Word>> getAllWords() {
            return mAllWords;
        }

        // 您必须在非UI线程上调用此函数，否则您的应用程序将引发异常。 Room确保您不会在主线程上执行任何长时间运行的操作，从而阻止了UI。
        //我们不能在主线程上运行插入，因此我们可以使用WordRoomDatabase中创建的ExecutorService来在后台线程上执行插入。
        void insert(Word word) {
            WordRoomDatabase.databaseWriteExecutor.execute(() -> {
                mWordDao.insert(word);
            });
        }
    }

    //创建一个WordViewModel
    public static class WordViewModel extends AndroidViewModel {
        //声明储存库
        private WordRepository repository;
        //声明LiveData数据变量
        private LiveData<List<Word>> allWords;

        //添加了一个私有成员变量来保存对存储库的引用。
        public WordViewModel(@NonNull Application application) {
            super(application);
            //创建储存库
            repository = new WordRepository(application);
            //使用存储库初始化LiveData。
            allWords = repository.getAllWords();
        }

        //获取WordList的LiveData
        LiveData<List<Word>> getAllWords() {
            return allWords;
        }

        //插入一条Word数据
        public void insert(Word word) {
            repository.insert(word);
        }
    }
}