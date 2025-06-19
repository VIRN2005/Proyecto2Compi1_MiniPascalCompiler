package Symbols;

import java.util.Objects;

public class Type {

    private String name;
    private int size;

    public Type(String name) {
        this.name = name;
        this.size = -1; // Set a default size value for types without a specified size
        this.basicType = determineBasicType(name);
    }
    public Type(String name, int lowerBound, int upperBound) {
        this.name = name;
        // Set the size to the difference between the upper and lower bounds plus 1
        this.size = upperBound - lowerBound + 1;
        this.basicType = determineBasicType(name);
    }


    // Enum to represent basic type categories, expandable as needed
    public enum BasicType {
        integer, string, boolean_, character, user_defined
    }

    private BasicType basicType;

    public Type(String name, int size) {
        this.name = name;
        this.size = size;
        this.basicType = determineBasicType(name);
    }
    public Type(String name, int size, BasicType basicType) {
        this.name = name;
        this.size = size;
        this.basicType = basicType;
    }

    private BasicType determineBasicType(String name) {
        try {
            if(name.contains("boolean")) return BasicType.boolean_;
            if(name.contains("char")) return BasicType.character;
            return BasicType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return BasicType.user_defined; // Assumes any unrecognized type is a user-defined type
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.basicType = determineBasicType(name);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isCompatible(Type other) {
        if (other == null) return false;

        switch (basicType) {
            case integer:
                return other.basicType == BasicType.integer;
            case string:
                return other.basicType == BasicType.string;
            case boolean_:
                return other.basicType == BasicType.boolean_;
            case user_defined:
                return other.basicType == BasicType.user_defined;
            case character:
                return other.basicType == BasicType.character;
            default:
                return false;
        }
    }

    public boolean isNumeric() {
        return basicType == BasicType.integer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return size == type.size && basicType == type.basicType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(basicType, size);
    }

    @Override
    public String toString() {
        return String.format("Type(name: %s, size: %d, basicType: %s)", name, size, basicType);
    }

}
