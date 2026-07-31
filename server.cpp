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
    char keybuffer[1024] = { 0 };
    recv(clientSocket, buffer, sizeof(buffer), 0);
    recv(clientSocket,keybuffer,sizeof(keybuffer),0);
    cout << "Encrypted Message from client: " << buffer<< endl;
    string decrypt = "";
    string encrypt = buffer;
    string skey = keybuffer;
    int key = stoi(skey);
    for (auto ch : encrypt) {

        int x = (ch - 'a' - key + 26) % 26;
        decrypt += char(x + 'a');
    }

    cout << "\nDecrypted String: " << decrypt << endl;

    // closing the socket.
    close(serverSocket);

    return 0;
}
