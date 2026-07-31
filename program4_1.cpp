//Affine Cipher
#include<bits/stdc++.h>
using namespace std;
int modInverse(int a, int mod) {
    for (int i = 1; i < mod; i++) {
        if ((a * i) % mod == 1)
            return i;
    }
    return -1;
}
int main(){
	string pt="";
	cout<<"Enter Plain Text"<<endl;
	cin>>pt;
	int a,b;
	cout<<"Enter a and b value"<<endl;
	cin>>a>>b;
	int mod=26;
	string ct="";
	cout<<"Cipher text:";
	for(char ch:pt){
		char x=(((ch-'a')*a)+b)%mod+'a';
		cout<<x;
		ct+=x;
	}
	string dt = "";
    cout << "\nDecrypted Text: ";
    int aInv=modInverse(a,mod);
    for (char ch : ct) {
        int val = (aInv * ((ch - 'a') - b + mod)) % mod;
        char x = val + 'a';
        cout << x;
        dt += x;
    }

	return 0;
}
