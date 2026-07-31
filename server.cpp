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
    int serverSocket = socket(AF_INET, SOCK_STREAM, 0);

    // specifying the address
    sockaddr_in serverAddress;
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(8080);
    serverAddress.sin_addr.s_addr = INADDR_ANY;

    // binding socket.
    bind(serverSocket, (struct sockaddr*)&serverAddress,
         sizeof(serverAddress));

    // listening to the assigned socket
    listen(serverSocket, 5);

    // accepting connection request
    int clientSocket = accept(serverSocket, nullptr, nullptr);

    // recieving data
    char buffer[1024] = { 0 };
    char abuffer[1024] = { 0 };
    char bbuffer[1024] = { 0 };
    recv(clientSocket, buffer, sizeof(buffer), 0);
    recv(clientSocket,abuffer,sizeof(abuffer),0);
    recv(clientSocket,bbuffer,sizeof(bbuffer),0);
    cout << "Encrypted Message from client: " << buffer<< endl;
    string decrypt = "";
    string encrypt = buffer;
    string sa = abuffer;
    string sb = bbuffer;
    int a = stoi(sa);
    int b = stoi(sb);
    int mod=26;
    string dt = "";
    string ct= buffer;
    int aInv=modInverse(a,mod);
    for (char ch : ct) {
        int val = (aInv * ((ch - 'a') - b + mod)) % mod;
        char x = val + 'a';
        dt += x;
    }

    cout << "\nDecrypted String: " << dt << endl;

    // closing the socket.
    close(serverSocket);

    return 0;
}
