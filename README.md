# Hetty HTTP Utilities
Hetty HTTP Utilities is a lightweight Java API primarily useful for parsing and creating raw HTTP requests and responses, along with sending and receiving them through TCP sockets. It gives you full control over HTTP packets that are being sent over the network and supports both HTTP and HTTPS. The project is created using eclipse and is ready to be imported. 

#### Features
- Supports HTTP 1.0 and 1.1
- Supports chunked transfer encoding (does not support trailers)
- Full control over HTTP packets (requests / responses)
- Parses and Generates HTTP Requests and Responses
- Parses and Generates HTTP Cookies with attributes
- Filter pipeline that allows changes to HTTP packets before being generated / sent to server
- HTTPCookieJar for storing cookies
- Uses a layered system so that you can choose how "low-level" you want your code to be
- HTTPConnection that established connection to server using TCP / SSL socket
- HTTPNavigator that follows redirects from 'Location' headers and handles cookies for you
- HTTPLoginRobot that logs you easily into a website using credentials
- HTML wrapper classes for HTML support
- Extremely lightweight (relies on no 3rd party libraries)
- Built-in support for Jsoup HTML parser
- Uses custom unchecked exceptions so that you do not need empty try-catch statements everywhere

#### Requirements
- v2.0 Requires Java 1.7 or newer
- v1.0 Requires Java 1.5 or newer

#### Binaries
You can find pre-compiled releases in the 'releases' folder.