package com.echecs.projet_integrateur.pkgActivites;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.echecs.projet_integrateur.R;
import com.echecs.projet_integrateur.pkgControleur.ControleurBluetooth;
import com.echecs.projet_integrateur.pkgControleur.Deplacement;
import com.echecs.projet_integrateur.pkgVue.Fenetre;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Activité qui gère les connexions bluetooths et affiche le jeu lorsqu'il y a connexion
 */
public class JeuBluetooth extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private static int DEMANDE_ACTIVATION_BT = 1;
    private static int DEMANDE_DECOUVERTE = 2;
    private BluetoothDevice appareilAConnecter;
    private Set<BluetoothDevice> pairedDevices;
    private static UUID MY_UUID = UUID.fromString("7f84fdc2-d582-4e80-992f-80830257b531");
    private BroadcastReceiver receiver;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private final static int OPTIONS_SERVEUR = 1;
    private final static int ACTION = 2;
    private boolean registered = false;
    private boolean couleur;
    private Handler handler;
    private Fenetre fenetre;
    private ArrayAdapter arrayAdapter;
    private ArrayList<BluetoothDevice> lstAppareils = new ArrayList<>();

    /**
     * appelé lors de la création, lance la vérification du bluetooth
     *
     * @param savedInstanceState technicalité du programme
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifierBluetooth();
    }

    /**
     * vérifie si le bluetooth est supporté
     * si oui, vérifie qu'il est activé et demande de l'activer s'il n'est pas activé pour ensuite demander de choisir à quel appareil se connecter
     * si non, retourne au menu principal
     */
    private void verifierBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non supporté", Toast.LENGTH_LONG).show();
            retournerAuMenu();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, DEMANDE_ACTIVATION_BT);
            } else {
                choisirAppareil();
            }
        }
    }

    /**
     * affiche la liste des appareils déjà jumelé au téléphone et permet d'en découvrir d'autres
     * lorsque l'utilisateur clique sur un appareil, commence le processus de connexion
     */
    private void choisirAppareil() {
        setContentView(R.layout.liste_appareils);
        ListView lstViewAppareils = (ListView) findViewById(R.id.listView_appareils);
        Button btnDecouverte = (Button) findViewById(R.id.button_rechercheAppareils);
        Button btnAnnuler = (Button) findViewById(R.id.button_annuler);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lstViewAppareils.setAdapter(arrayAdapter);
        pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            lstAppareils.add(device);
            arrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
        lstViewAppareils.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                appareilAConnecter = lstAppareils.get(position);
                connecterAppareils();
            }
        });
        btnDecouverte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commencerDecouverteAppareils();
            }
        });
        btnAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registered) {
                    unregisterReceiver(receiver);
                }
                retournerAuMenu();
            }
        });
    }

    /**
     * permet de découvrir des appareils non jumelés au téléphone
     */
    private void commencerDecouverteAppareils() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                registered = false;
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice appareil = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    lstAppareils.add(appareil);
                    arrayAdapter.add(appareil.getName() + "\n" + appareil.getAddress());
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    unregisterReceiver(this);
                    registerReceiver(this, filter);
                    registered = true;
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        registered = true;
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(discoverableIntent, DEMANDE_DECOUVERTE);
    }

    /**
     * appelé lorsqu'une activité autre se termine après avoir été appelée grâce à startActivityForResult()
     *
     * @param requestCode la raison du lancement de l'activité
     * @param resultCode  le résultat de l'activité
     * @param data        permet de faire passer des données d'une activité à l'autre
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEMANDE_ACTIVATION_BT) {
            if (resultCode == RESULT_OK) {
                choisirAppareil();
            } else if (resultCode == RESULT_CANCELED) {
                retournerAuMenu();
            }
        } else if (requestCode == DEMANDE_DECOUVERTE) {
            if (resultCode == RESULT_CANCELED) {
                unregisterReceiver(receiver);
                retournerAuMenu();
            } else {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    /**
     * demande le rôle que veut jouer l'utilisateur dans la connexion puis la commence
     */
    private void connecterAppareils() {
        initialiserHandler();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        acceptThread = new AcceptThread();
                        acceptThread.start();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        connectThread = new ConnectThread(appareilAConnecter);
                        connectThread.start();
                        break;

                    case DialogInterface.BUTTON_NEUTRAL:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Quel rôle désirez-vous avoir dans la connexion?")
                .setPositiveButton("Serveur", dialogClickListener)
                .setNegativeButton("Client", dialogClickListener)
                .setNeutralButton("Annuler", dialogClickListener)
                .show();
    }

    /**
     * initialise un handler qui permet la communication entre les threads de connexion et le thread de l'interface utilisateur
     */
    private void initialiserHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case OPTIONS_SERVEUR:
                        if ((boolean) msg.obj) {
                            setContentView(R.layout.options_serveur);
                            initialiserOptionsServeur();
                        } else {
                            setContentView(R.layout.attente_bluetooth);
                        }
                        break;
                    case ACTION:
                        if (msg.obj instanceof Boolean[]) {
                            initialiserFenetre(((Boolean[]) msg.obj)[0], ((Boolean[]) msg.obj)[1]);
                        } else if (msg.obj instanceof Deplacement) {
                            ((ControleurBluetooth) fenetre.getControleur()).recevoirDeplacement((Deplacement) msg.obj);
                        }
                        break;
                }
            }
        };
    }

    /**
     * initialise la fenêtre
     *
     * @param couleur  la couleur de l'utilisateur
     * @param bughouse si le mode bughouse est activé
     */
    private void initialiserFenetre(boolean couleur, boolean bughouse) {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        fenetre = new Fenetre(this, size.x, size.y, getFragmentManager(), couleur, connectedThread, bughouse);
        setContentView(fenetre);
    }

    /**
     * affiche les options données au serveur au début de la connexion (choix de la couleur et du mode de jeu)
     */
    private void initialiserOptionsServeur() {
        Button btnCommencer = (Button) findViewById(R.id.button_commencerPartie_Bluetooth);
        final RadioButton radioBlancs = (RadioButton) findViewById(R.id.radioButton_Bluetooth_blancs);
        final RadioButton radioNoirs = (RadioButton) findViewById(R.id.radioButton_Bluetooth_noirs);
        final RadioButton radioAleatoire = (RadioButton) findViewById(R.id.radioButton_Bluetooth_aleatoire);
        final CheckBox chkBughouse = (CheckBox) findViewById(R.id.checkBox_bughouse);
        btnCommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioBlancs.isChecked()) {
                    couleur = true;
                } else if (radioNoirs.isChecked()) {
                    couleur = false;
                } else if (radioAleatoire.isChecked()) {
                    Random aleatoire = new Random();
                    int nbCouleur = aleatoire.nextInt(2);
                    if (nbCouleur == 0) {
                        couleur = true;
                    } else {
                        couleur = false;
                    }
                }
                initialiserFenetre(couleur, chkBughouse.isChecked());
                Boolean[] tab = {!couleur, chkBughouse.isChecked()};
                connectedThread.write(tab);
            }
        });
    }

    /**
     * gère la connexion avec l'autre cellulaire et termine les thread de recherche
     *
     * @param socket  le socket de la connexion
     * @param serveur si l'appareil est le serveur dans la connexion
     */
    private void gererConnexion(BluetoothSocket socket, boolean serveur) {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        Message msg = new Message();
        msg.setTarget(handler);
        msg.obj = serveur;
        msg.what = OPTIONS_SERVEUR;
        msg.sendToTarget();
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    /**
     * permet de retourner au menu et annule la connexion
     */
    @Override
    public void onBackPressed() {
        if (registered) {
            unregisterReceiver(receiver);
            registered = false;
        }
        retournerAuMenu();
    }

    /**
     * finis l'activité et annule les threads de connexion ou de recherche
     */
    public void retournerAuMenu() {
        Intent intent = new Intent();
        intent.setAction("finish_activity");
        sendBroadcast(intent);
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        intent = new Intent(JeuBluetooth.this, Menu.class);
        startActivity(intent);
        finish();
    }

    /**
     * permet d'afficher qui a gagné ou s'il y a nulle
     *
     * @param etat passe en paramètre l'identité du vainqueur
     */
    public void victoire(int etat) {
        String message;
        switch (etat) {
            case Fenetre.VICTOIRE_BLANCS:
                message = "Victoire des blancs!";
                break;
            case Fenetre.VICTOIRE_NOIRS:
                message = "Victoire des noirs!";
                break;
            case Fenetre.NULLE:
                message = "Partie nulle.";
                break;
            default:
                message = "";
                break;
        }
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        retournerAuMenu();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Retour au menu", dialogClickListener)
                .show();
    }

    /**
     * Classe permettant au côté serveur de la connexion d'accepter une connexion
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        /**
         * initialise le thread
         */
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Test", MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        /**
         * cherche une connexion
         * lorsqu'il en trouve une, l'envoie à gererConnexion qui permet de communiquer avec l'autre cellulaire
         */
        public void run() {
            BluetoothSocket socket = null;
            boolean run = true;
            while (run) {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                if (socket != null) {
                    gererConnexion(socket, true);
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    run = false;
                }
            }
        }

        /**
         * ferme le socket
         */
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Classe permettant au côté client de la connexion de se connecter au serveur
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        /**
         * initialise le thread
         *
         * @param device l'appareil auquel se connecter
         */
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            this.device = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = tmp;
        }

        /**
         * annule la découverte d'autres appareil et tente de se connecter à un appareil
         * lorsqu'il y a connexion, appelle gererConnexion qui permet de communiquer avec l'autre cellulaire
         */
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }
            connectThread = null;
            gererConnexion(socket, false);
        }

        /**
         * ferme le socket
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * gère la connexion entre les 2 appareils et permet d'envoyer des données
     */
    public class ConnectedThread extends Thread implements Serializable {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        /**
         * initialise les stream permettant d'envoyer ou de recevoir du data
         *
         * @param socket le socket qui s'est connecté peu importe s'il provient du ConnectThread ou du AcceptThread
         */
        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        /**
         * vérifie si des données ont été envoyées
         * s'il y en a, les déchiffrent puis les envoie au thread utilisateur via le handler
         */
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    Object obj = deserialize(buffer);
                    Message msg = new Message();
                    msg.setTarget(handler);
                    msg.obj = obj;
                    msg.what = ACTION;
                    msg.sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /**
         * déchiffre les données sous forme de bytes et les retransforment en objet
         *
         * @param array tableau de bytes qui contient les données
         * @return l'objet provenant des bytes
         */
        private Object deserialize(byte[] array) {
            ByteArrayInputStream b = new ByteArrayInputStream(array);
            ObjectInput in = null;
            Object obj = null;
            try {
                in = new ObjectInputStream(b);
                obj = in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return obj;
        }

        /**
         * code un message en bytes et l'envoie à l'autre cellulaire
         *
         * @param obj l'objet à envoyer
         */
        public void write(Object obj) {
            byte[] bytes = serialize(obj);
            try {
                if (bytes != null) {
                    outputStream.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * change un objet en tableau de bytes
         *
         * @param obj l'objet à envoyer
         * @return le tableau de bytes
         */
        private byte[] serialize(Object obj) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutput out = null;
            byte[] array = null;
            try {
                out = new ObjectOutputStream(b);
                out.writeObject(obj);
                array = b.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return array;
        }

        /**
         * ferme le socket de la connexion
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
