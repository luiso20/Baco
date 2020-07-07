package es.puntocomaapps.baco;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseDatosFirebase {
    private DatabaseReference dbReference;

    public BaseDatosFirebase() {
        this.dbReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDbReference() {
        return dbReference;
    }

    public void setDbReference(DatabaseReference dbReference) {
        this.dbReference = dbReference;
    }
}
