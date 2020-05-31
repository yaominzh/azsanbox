#include <iostream>
using namespace std;
void fun1(int a, int b){
    a = 10;
    b = 1;
    return ;
}
void fun2(int &a, int &b){
    a = 10;
    b = 1;
    return ;
}
int main(){
    int a = 1, b = 10;
    cout << a << " " << b << endl;
    fun1(a,b);
    cout << a << " " << b << endl;
    fun2(a,b);
    cout << a << " " << b << endl;
    return 0;
}

/*
g++ chap01p06.cpp -o chap01p06 && "/Users/allenzhang/repo/azsanbox/ctemp/"chap01p06
*/