#include <cstring>
#include <iostream>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>
#include <bits/stdc++.h>
using namespace std;


int modInverse(int a, int mod) {
    for (int i = 1; i < mod; i++) {
        if ((a * i) % mod == 1)
            return i;
    }
    return -1;
}

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

        string pt="";
        cout<<"Enter Plain Text : ";
        cin>>pt;
        int a,b;
        cout<<"Enter a and b value : ";
        cin>>a>>b;
        int mod=26;
        string ct="";
        for(char ch:pt){
                char x=(((ch-'a')*a)+b)%mod+'a';
                ct+=x;
	}
	cout<<"Cipher text : "<< ct<<endl;


    // sending data
    
    string sa= to_string(a);
    string sb= to_string(b);
    const char* message = ct.c_str();
    send(clientSocket, message, strlen(message), 0);
    usleep(100000);
    const char* cta = sa.c_str();
    send(clientSocket, cta, strlen(cta),0);
    usleep(100000);
    const char* ctb = sb.c_str();
    send(clientSocket,ctb, strlen(ctb),0);
    
    // closing socket
    close(clientSocket);

    return 0;
}
