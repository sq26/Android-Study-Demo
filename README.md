# Android-Study-Demo
个人对Android学习的代码块

//罕见报错及解决方法
报错:Invoke-customs are only supported starting with Android O
解决:这个问题是jdk设置问题,需要在项目设置中指定jdk的位置,和项目设置app的properties选项卡中设置 sourceCompatibility的java版本和targetCompatibility的java版本
一定要与jdk的版本一致
