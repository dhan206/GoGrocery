package edu.uw.dhan206.gogrocery;

import com.google.firebase.database.DatabaseReference;

public class Item {
    public String name;
    public String description;
    public String address;
    public String addedBy;
    public String locationName;

    public static void addItemToDb(DatabaseReference db, Item item) {
        db.child("name").setValue(item.name);
        db.child("description").setValue(item.description);
        db.child("address").setValue(item.description);
        db.child("addedBy").setValue(item.addedBy);
        db.child("locationName").setValue(item.locationName);

    }
}
