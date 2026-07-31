#include <cstring>
#include <iostream>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>
#include <bits/stdc++.h>
using namespace std;


int main()
{
    // creating socket
    int clientSocket = socket(AF_INET, SOCK_STREAM, 0);

    // specifying address
    sockaddr_in serverAddress;
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(8080);
    serverAddress.sin_addr.s_addr = INADDR_ANY;

    // sending connection request
    connect(clientSocket, (struct sockaddr*)&serverAddress,
            sizeof(serverAddress));

    // sending data
    string s = "";
    cout << "Enter string to be sent : ";
    cin >> s;

    int key;
    cout << "Enter Key : ";
    cin >> key;

    string encrypt = "";

    for (auto ch : s) {

        int x = (ch - 'a' + key) % 26;
        encrypt += char(x + 'a');
    }
    cout<<"Encrypted Text : "<<encrypt<<endl;
    
    string skey= to_string(key);
    const char* message = encrypt.c_str();
    send(clientSocket, message, strlen(message), 0);
    usleep(100000);
    const char* enkey = skey.c_str();
    send(clientSocket, enkey, strlen(enkey),0);
    
    // closing socket
    close(clientSocket);

    return 0;
}
