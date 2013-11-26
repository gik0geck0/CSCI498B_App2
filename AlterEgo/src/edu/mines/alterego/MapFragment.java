package edu.mines.alterego;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.mines.alterego.TCPClient;

/**
 * Description: This class defines the functionality for the map fragment.
 * It handles starting the map activity
 * @author Matt Buland, Maria Deslis, Eric Young
 *
 */

public class MapFragment extends Fragment implements TCPClient.OnMessageReceived {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (GameActivity.mCharId == -1) {
            // Yes, this is annoying, but it'll make an error VERY obvious. In
            // testing, I have never seen this toast/error message. But ya never
            // know
            Toast.makeText(getActivity(), "No character. Please make one!", Toast.LENGTH_SHORT)
                    .show();
            Log.e("AlterEgo:MapFragment", "No valid character. The user needs to make one.");
        }

        // Inflate the layout for this fragment
        View mapView = inflater.inflate(R.layout.map_fragment,
                container, false);

        final EditText msgEdit = (EditText) mapView.findViewById(R.id.message_edit);
        final Button sendMsg = (Button) mapView.findViewById(R.id.send_msg);

        final TCPClient tcpClient = new TCPClient(this);

        sendMsg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                tcpClient.sendMessage(msgEdit.getText().toString());
            }
        });

        return mapView;
    }

    @Override
    public void messageReceived(String message) {
        Log.d("AlterEgo::MapFragment::Msging", "Message received: " + message);
    }
}
