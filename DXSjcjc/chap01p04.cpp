#include <iostream>
using namespace std;
int main(){
    int i = 10;
    cout <<"global " << i << endl;
    for(int i =0 ; i<3; i++){
        cout <<"local " << i << endl;
    }
    i++;
    cout << "global " << i << endl;
    return 0;

}

/*
g++ chap01p04.cpp -o chap01p04 && "/Users/allenzhang/repo/azsanbox/ctemp/"chap01p04
*/