package com.fundora.model;

/**
 * Module I & II: Enum representing types of Fundora Groups
 * - FLATMATE_RECURRING: Monthly rent, electricity, cook, groceries
 * - TRIP_EVENT: Vacation, weekend outings, campus events
 */
public enum GroupType {
    FLATMATE_RECURRING("Flatmate & Monthly Recurring"),
    TRIP_EVENT("Trip, Outing & Campus Event");

    private final String displayName;

    GroupType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
