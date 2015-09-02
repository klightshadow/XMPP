package com.miiixer.xmpptest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.miiixer.xmpptest.adapter.ChatAdapter;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import java.io.IOException;
import java.util.Collection;


public class MainActivity extends AppCompatActivity {

    private static final String HOST = "xmpp.v-ki.net";
    private static final String ACCOUNT = "lightshadow";
    private static final String PASSWORD = "ken8546123";
    private static final int FROM_ME = 0;
    private static final int FROM_OTHER = 1;

    private EditText chatWith, chatContent;
    private Button send;
    private ListView chatList;
    private ChatAdapter chatAdapter;

    private XMPPConnection xmppConnection;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatWith = (EditText)findViewById(R.id.et_chatWith);
        chatContent = (EditText)findViewById(R.id.et_chatContent);

        send = (Button)findViewById(R.id.btn_Send);

        chatList = (ListView)findViewById(R.id.lv_chatList);

        chatAdapter = new ChatAdapter(this);
        chatList.setAdapter(chatAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = chatContent.getText().toString();
                String name = chatWith.getText().toString();

                Message message = new Message(name, Message.Type.chat);
                message.setBody(content);
                if(xmppConnection != null) {
                    try {
                        xmppConnection.sendPacket(message);
                        chatContent.setText("");
                        chatAdapter.add("Me", content, FROM_ME);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        connect();
    }

    private void connect() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Connecting to server...", "Please wait...", false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST);//new ConnectionConfiguration(HOST);
                XMPPConnection connection = new XMPPTCPConnection(connConfig);
                try {
                    connection.connect();
                    Log.e("MainActivity",
                            "Connected to " + connection.getHost());
                } catch (SmackException e) {
                    Log.e("MainActivity", e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    Log.e("MainActiviy", "Failed to connect to " + connection.getHost());
                    Log.e("MainActivity", e.toString());
                    setConnection(null);
                }
                try {
                    connection.login(ACCOUNT, PASSWORD);

                    Presence presence = new Presence(Presence.Type.available);
                    connection.sendPacket(presence);
                    setConnection(connection);

                    Roster roster = connection.getRoster();
                    Collection<RosterEntry> entries = roster.getEntries();
                    for(RosterEntry entry : entries) {
                        Log.e("MainActivity",
                                "--------------------------------------");
                        Log.e("MainActivity", "RosterEntry " + entry);
                        Log.e("MainActivity",
                                "User: " + entry.getUser());
                        Log.e("MainActivity",
                                "Name: " + entry.getName());
                        Log.e("MainActivity",
                                "Status: " + entry.getStatus());
                        Log.e("MainActivity",
                                "Type: " + entry.getType());
                        Presence entryPresence = roster.getPresence(entry.getUser());

                        Log.e("MainActivity", "Presence Status: "
                                + entryPresence.getStatus());
                        Log.e("MainActivity", "Presence Type: "
                                + entryPresence.getType());
                        Presence.Type type = entryPresence.getType();
                        if(type == Presence.Type.available) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (XMPPException e) {
                    Log.e("MainActivity", "Failed to log in as "
                            + ACCOUNT);
                    Log.e("MainActivity", e.toString());
                    setConnection(null);
                } catch (SmackException | IOException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();

    }

    private void setConnection(XMPPConnection connection) {
        xmppConnection = connection;
        if(connection != null) {
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                    final Message message = (Message)packet;
                    if(message.getBody() != null) {
                        final String name = StringUtils.parseBareAddress(message.getFrom());
                        //Log.e("MainActivity", "Text Recieved " + message.getBody() + " from " + name);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.add(name, message.getBody().toString(), FROM_OTHER);
                            }
                        });

                    }
                }
            }, filter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
