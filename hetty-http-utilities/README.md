# Hetty HTTP Utilities
Hetty HTTP Utilities is a lightweight Java API primarily useful for parsing and creating raw HTTP requests and responses, along with sending and receiving them through TCP sockets. It gives you full control over HTTP packets that are being sent over the network and supports both HTTP and HTTPS. The project is created using eclipse and is ready to be imported. 

#### Features
- Full control over HTTP packets (requests / responses)
- Parses and Generates HTTP Requests and Responses
- Parses and Generates HTTP Cookies with attributes
- Filter pipeline that allows changes to HTTP packets before being generated / sent to server
- HTTPConnection that established connection to server using TCP / SSL socket
- HTTPNavigator that sends / receives multiple requests if server sends back 'Location' or 'Cookie' headers
- Extremely lightweight (relies on no 3rd party libraries)

#### Requirements
- Requires Java 1.5 or newer

#### Binaries
You can find pre-compiled releases in the 'releases' folder.