package edu.uw.dhan206.gogrocery;


import com.google.firebase.database.DatabaseReference;

public class Item {
    public String id;
    public String name;
    public String description;
    public String address;
    public String addedBy;
    public String locationName;
    public boolean done = false;

    public static void addItemToDb(DatabaseReference db, Item item) {
        if (item.name == null) throw new IllegalArgumentException("Item name cannot be null.");

        if (item.description == null) item.description = "";
        if (item.addedBy == null) item.description = "Anonymous";

        db.child("name").setValue(item.name);
        db.child("description").setValue(item.description);
        db.child("addedBy").setValue(item.addedBy);
        db.child("done").setValue(item.done);
        db.child("id").setValue(item.id);


        if (item.address != null && item.locationName != null) {
            db.child("address").setValue(item.address);
            db.child("locationName").setValue(item.locationName);
        }
    }
}
