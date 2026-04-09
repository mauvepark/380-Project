package edu.ucalgary.oop;

public class Location {
    private int id;
    private String name;
    private String address;
    private DisasterVictim[] occupants;
    private Supply[] supplies;

    // constructors
    public Location(int id, String name, String address) {
        setId(id);
        setName(name);
        setAddress(address);
        this.occupants = new DisasterVictim[0];
        this.supplies = new Supply[0];
    }

    public Location(String name, String address) {
        setName(name);
        setAddress(address);
        this.occupants = new DisasterVictim[0];
        this.supplies = new Supply[0];
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Location ID cannot be negative.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Location address cannot be null or empty.");
        }
        this.address = address.trim();
    }

    public DisasterVictim[] getOccupants() {
        return occupants;
    }

    public void setOccupants(DisasterVictim[] occupants) {
        this.occupants = occupants != null ? occupants.clone() : new DisasterVictim[0];
    }

    public Supply[] getSupplies() {
        return supplies;
    }

    public void setSupplies(Supply[] supplies) {
        this.supplies = supplies != null ? supplies.clone() : new Supply[0];
    }

    // add occupant at location
    public void addOccupant(DisasterVictim occupant) {
        if (occupant == null) {
            throw new IllegalArgumentException("Occupant cannot be null");
        }

        DisasterVictim[] newOccupants = new DisasterVictim[occupants.length + 1];
        System.arraycopy(occupants, 0, newOccupants, 0, occupants.length);
        newOccupants[occupants.length] = occupant;
        this.occupants = newOccupants;
    }

    // remove occupant from location
    public void removeOccupant(DisasterVictim occupant) {
        if (occupant == null) {
            throw new IllegalArgumentException("Occupant cannot be null");
        }

        int index = -1;
        for (int i = 0; i < occupants.length; i++) {
            if (occupants[i].equals(occupant)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Occupant not found in location");
        }

        DisasterVictim[] newOccupants = new DisasterVictim[occupants.length - 1];

        System.arraycopy(occupants, 0, newOccupants, 0, index);

        System.arraycopy(occupants, index + 1, newOccupants, index, occupants.length - index - 1);

        this.occupants = newOccupants;
    }

    // add supply to location
    public void addSupply(Supply supply) {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }

        Supply[] newSupplies = new Supply[supplies.length + 1];
        System.arraycopy(supplies, 0, newSupplies, 0, supplies.length);
        newSupplies[supplies.length] = supply;
        this.supplies = newSupplies;
    }

    // remove supply from location
    public void removeSupply(Supply supply) {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }

        int index = -1;
        for (int i = 0; i < supplies.length; i++) {
            if (supplies[i].equals(supply)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Supply not found in location");
        }

        Supply[] newSupplies = new Supply[supplies.length - 1];

        System.arraycopy(supplies, 0, newSupplies, 0, index);

        System.arraycopy(supplies, index + 1, newSupplies, index, supplies.length - index - 1);
        
        this.supplies = newSupplies;
    }
}