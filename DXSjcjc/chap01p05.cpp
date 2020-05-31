#include <iostream>
using namespace std;

const int N = 10;

int main(){
    int a[N][N]; 
    for(int i=0; i<N; i++){
        for(int j = 0; j<N; j++){
            a[i][j] = i+j;
            cout <<" " << a[i][j];
        }
        cout<<endl;
    }
    return 0;
}

/*
g++ chap01p05.cpp -o chap01p05 && "/Users/allenzhang/repo/azsanbox/ctemp/"chap01p05
*/