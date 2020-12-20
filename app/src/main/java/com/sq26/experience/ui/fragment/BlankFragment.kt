package com.sq26.experience.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.sq26.experience.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * 创建此片段的一个实例。
 */
public class BlankFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.text)
    TextView text;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlankFragment() {
        // 必需的空公共构造函数
    }

    /**
     * 使用此工厂方法创建一个新的实例
     * 该片段使用提供的参数。
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return 片段BlankFragment的新实例。
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        //通过setArguments的方式保存的参数不会应为屏幕旋转或是其他重新创建的情况而丢失
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //恢复参数
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 创建此片段的布局
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        //在fragment中获取navController
        navController = Navigation.findNavController(container);
        ButterKnife.bind(this, view);
        return view;
    }

    private void start() {
        //指定跳转行为,进行跳转
        navController.navigate(R.id.action_blankFragment_to_blank2Fragment);
    }

    @OnClick(R.id.text)
    public void onViewClicked() {
        start();
    }
}