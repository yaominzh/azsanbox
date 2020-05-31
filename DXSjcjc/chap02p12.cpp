#include <iostream>
using namespace std;
int main(){
    int x,y,z;
    cout<<"x\t" << "y\t" << "z " << endl;
    for(x = 0; x<=25; x++){
        y = 100 - 4 * x;
        if(y % 7 == 0 && y >= 0){
            y /= 7;
            z = 100 - x - y;
            if ( z % 3 == 0 && 3 * x + 5 * y + z / 3 == 100)
                cout << x << " " << y << " " << z << endl;
        }
    }
}