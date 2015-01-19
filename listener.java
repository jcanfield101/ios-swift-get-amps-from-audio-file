/**
* Start TCP listener
* @param start TCP listener
* @throws IOException
*/

    private void ConnectTCPReceiver() throws IOException{
        serverThread = new ServerThread();
        serverThread.setupAndRun(portServer);
        this.socketServer = serverThread.getSocket();
    }



@SuppressLint("NewApi")

class ServerThread implements Runnable {

public int port;

public Socket socket;


private Socket getSocket(){

return socket;

}


private ServerSocket getServerSocket(){

return serverSocket;

}


private void setupAndRun(int port){

this.port = port;

this.startServer();

}



private void startServer(){

asyncTcpServer = new AsyncTask<Void, Void, Void>(){

@Override

    protected Void doInBackground(Void... params) {

try {

    serverSocket = new ServerSocket(port);

} catch (IOException e) {

e.printStackTrace();

}


while (!Thread.currentThread().isInterrupted() && serverActive) {

    Looper.prepare();

try {

    socket = serverSocket.accept();

    CommunicationThread commThread = new CommunicationThread(socket);

new Thread(commThread).start();

} catch (IOException e) {

onSystemConnectionStatusListener.OnSystemError("Unable to start TCP server");

e.printStackTrace();

}


Looper.loop();

}

  return null;

    }


    @Override

protected void onPostExecute(Void result) {

      super.onPostExecute(result);

      asyncTcpServer = null;

    }





};

if (Build.VERSION.SDK_INT >= 11) asyncTcpServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

else asyncTcpServer.execute();

}


@Override

public void run() {


}

}


class CommunicationThread implements Runnable {

public Socket clientSocket;

public Handler updateConversationHandler = new Handler();

public CommunicationThread(Socket clientSocket) {

this.clientSocket = clientSocket;

try {

input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

} catch (IOException e) {

e.printStackTrace();

}

}



@Override

public void run() {

while (!Thread.currentThread().isInterrupted() && serverActive) {

String read;

try {

read = input.readLine();

updateConversationHandler.post(new updateUIThread(read));

} catch (IOException e) {

e.printStackTrace();

}

}

}

}



class updateUIThread implements Runnable {

public String msg="";

public updateUIThread(String str) {

this.msg = str;

}


@Override

public void run() {

if (msg != null){

if(onMessageListener != null)

                    onMessageListener.onRawMessageReceived(msg);



}

}

}




© 2015 Microsoft Terms Privacy & cookies Developers English (United States)

