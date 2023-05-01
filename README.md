# Client-Server-TCP-IP-Web-Server

In this simple demo, I have created a Client-Server TCP/IP based Web Server that takes in requests from the client and displays appropriate HTTP Status Codes depending upon the request.

This demonstration of the TCP/IP uses TCP sockets to connect and communicate. Every connection that we make to the Internet is carried out on communication channels which are mostly designed on the principles of the TCP/IP architecture protocol. In this project, I have tried to break down the implementation of the same in such a way that someone who is new to the field of Distributed Systems could also comprehend it.

### Significance of Distributed Systems in this context

Distributed Systems is the need of the hour. Every modern application is built on the principles of Distributed Systems; no application these days is stand-alone. Any application that one may consider, for instance the popular email applications that we use on our smartphones on a daily basis, is technically an example of it. The server is physically located in some remote area that is inaccessible to the user physically. But the architecture is designed in sucha a manner that the user gets the idea that he/she is actually owning the application. What do I mean by ***"owning the application"*** is the feeling that the application is made solely for one person.

> **Client** ---> **Create a socket** ---> **Request connection** ---> **Server**

> **Server** ---> **Connect to the socket** ---> **Notify client**

> **Client** ---> **Request data** ---> **Server**

> **Server** ---> **Search for the requested data** ---> **Send back along with HTTP Status Code** ---> **Client**

> **Client** ---> **Close the socket**

As the term suggests, Distributed Systems are built over a set of systems. There is a central server which coukd also have a set of servers to be put to use to distribute the incoming traffic. The users send HTTP requests to the server through the connection medium. To do this, the machine on the client side opens a socket and sends a connection request to the server over a connection medium in the form of HTTP requests. Upon receiving the request, the server connects to the socket and the connection is comlete. The client can then send in data requests to the server. These could be transient data sets, webpages, forms, etc. Once the server fetches the required information, it sends it to the client along with appropriate HTTP Status Codes.

### HTTP Status Codes

HTTP Status Codes are responses that are sent from the server to the clients to specify the type of problem if any. A HTTP status code is sent even if there is no problem and the webpage or the data requested has been processed successfully. This is the success message. Other status codes also exist which tell us what the problem basically is and why the data could not be fetched. Some of the common HTTP Status Codes are as follows :-

- 100 series
> The 100 series HTTP Status Codes are called ***Informational Reponse*** that are sent out to the client to notify them that the request has been received and the search for the requested data has been started. The request is still being processed and the client needs to wait for a final response from the server.

- 200 series
> The 200 series HTTP Status Codes are called ***Success Reponse*** that are sent out to the client to notify them that the request has been understood, received, accepted and carried out successfully. It indicates that the request was fulfilled by the server. This is one of the very common status codes that we receive from the server. Whenever we receive a webpage successfully that we had requested for, we technically get the HTTP Status Code ***200 OK*** from the server.

- 300 series
> The 300 series HTTP Status Codes are called ***Redirection Reponse*** that are sent out to the client to notify them that they need to take further action on the made request.

- 400 series
> The 400 series HTTP Status Codes are called ***Client Error*** that are sent out to the client to notify them that the request made has an issue or cannot be completed because, for instance, the requested data does not exist, or cannot be proecessed or provided.
