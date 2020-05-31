#include <iostream>
using namespace std;
int main(){
    int x,y,z;
    cout<<"x\t" << "y\t" << "z " << endl;
    for(x = 0; x<=100; x++){
        for(y = 0; y<=100; y++){
            for(z = 0; z<=100; z+=3){
                if(x+y+z == 100 && 3*x + 5*y +z /3 ==100)
                cout<<x << "\t" << y << "\t" << z<< endl;
            }
        }
    }
}