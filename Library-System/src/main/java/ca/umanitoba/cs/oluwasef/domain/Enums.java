package ca.umanitoba.cs.oluwasef.domain;


enum ContactType{EMAIL,PHONE, ADDRESS} // Represents the different types of contact information a Member may have.

enum AccountStatus{ACTIVE, SUSPENDED, BORROWING_BLOCKED } // Represents the various status of account.

enum MediaCategory {CHILDREN, FANTASY, SCIENCE, HISTORY,OTHER} // Represents the various media category.

enum CopyStatus {AVAILABLE, ON_LOAN, LOST, REPAIR}// various copy status



enum CellType {
    COMPUTER_AREA, CORRIDOR, DESK, EMPTY,
    ENTRANCE, ROOM, SHELVES_CHILDREN, SHELVES_FANTASY, SHELVES_SCIENCE, WALL

} // various cell types