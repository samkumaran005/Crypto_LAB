#include <bits/stdc++.h>
using namespace std;

int main() {
    string s = "";
    cin >> s;

    int key;
    cin >> key;

    string encrypt = "";
    string decrypt = "";

    cout << "Encryption:\n";
    for (auto ch : s) {
        cout << "Plain Text: ";
        cout << ch << " - " << (ch - 'a') << endl;

        int x = (ch - 'a' + key) % 26;
        encrypt += char(x + 'a');

        cout << "Encrypted Text: ";
        cout << char(x + 'a') << " - " << x << endl;
    }

    cout << "\nEncrypted String: " << encrypt << endl;

    cout << "\nDecryption:\n";
    for (auto ch : encrypt) {
        cout << "Encrypted Text: ";
        cout << ch << " - " << (ch - 'a') << endl;

        int x = (ch - 'a' - key + 26) % 26;
        decrypt += char(x + 'a');

        cout << "Decrypted Text: ";
        cout << char(x + 'a') << " - " << x << endl;
    }

    cout << "\nDecrypted String: " << decrypt << endl;

    return 0;
}
