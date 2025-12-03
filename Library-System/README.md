---
title: Library System Project!
author: Seyi Fashola(oluwasef@myumanitoba.ca)
date: Fall 2025

---
# Domain model

## Resources
* Patrick Dubois lecture slides.
* Franklin Bristow youtube video <https://www.youtube.com/live/2faoHFku-5I?si=QjmU_HlgLfWip8XQ>

## Diagram
This project models a **Library System** capable of managing:
- Libraries → Media items (Books, DVDs, Games) and Resources (Rooms, Equipment)
- Members → Loans, Holds, and Reviews
- Design by Contract implemented via Guava `Preconditions`
- Automated build via **Maven** (`mvn compile`, `mvn exec:java`)

```mermaid
classDiagram
  direction LR    
  class LibrarySystem{
    +name: String
    +addLibrary(l: Library)
    +addMember(m: Member)
    +findMember(id: UUID): Member
    +findLibrary(id: UUID): Library
    
  }
  note for Obstacle "Invariant Properties:
    <ul>
    <li> name != null 
    <li> name.length > 0
    </ul>"

  LibrarySystem --* Library
  LibrarySystem --* Member 

  class Library{
    +id: UUID
    +name: String
    +address: String
    +inventory: Set<Media>
    +resources: Set<Resource>
    +map: FloorMap
  }
  note for Library "Invariant Properties :
    <ul>
    <li> id != null
    <li> name != null 
    <li> name.length > 0
    <li> address != null 
    <li> address.length > 0
    <li> map != null
    
    </ul>"
  Library --* FloorMap
  Library --*  Media 
  Library  --* Resource 

  class Member{
    +id: UUID
    +name: String
    +contacts: Set<ContactInfo>
    +status: AccountStatus
    +reviews: List<Review>
    +holds: Set<Hold>
    +loans: Set<Loan>
  }
  note for Member "Invariant Properties:
    <ul> 
    <li> id != null
    <li> name != null 
    <li> name.length > 0
    <li> status != null
    </ul>"
  Member    --> AccountStatus 
  Member  --o ContactInfo
  Member  --> Loan
  Member  -->  Hold
  Member  -->  Review  

  class ContactInfo{
    +type: ContactType
    +value: String
    +preferred: boolean
  }
  note for ContactInfo "Invariant Properties:
    <ul>
    <li> type != null
    <li> value != null
    <li> value.length > 0
    </ul>"
  ContactInfo --> ContactType


  class Media{
    <<abstract>>
    +id: UUID
    +title: String
    +creator: String
    +category: MediaCategory
    +copies: Set<Copy>
    +waitlist: Queue<Member>
    +reviews: List<Review>
  }
  note for Media "Invariant Properties:
    <ul>
    <li> id != null
    <li> title != null 
    <li> title.length > 0
    <li> creator != null 
    <li> creator.length > 0
    <li> category != null
    </ul> "

  Media --* Copy
  Media --> MediaCategory
  Media --> Book
  Media --> DVD
  Media --> Game  
  Media --> Hold 
  Media --o Review


  class Book{
    +isbn: String
  }
   note for Book "Invariant Properties:
    <ul>
    <li> isbn != null
    <li> isbn.length > 0
    </ul> "

  class DVD{
    +regionCode: String
  }
  note for DVD "Invariant Properties:
    <ul> 
    <li> regionCode != null 
    <li> regionCode.length > 0
    </ul>"

  class Game{
    +platform: String
  }
  note for Game "Invariant Properties:
    <ul>
    <li> platform != null
    <li> platform.length > 0
    </ul>"

  class Copy{
    +barcode: String
    +location: String
    +status: CopyStatus
  }
     note for Copy "Invariant Properties:
<ul>
<li> barcode != null 
<li> barcode.length > 0
<li> location != null 
<li> location.length > 0
<li> status != null

</ul>"
  Copy --> CopyStatus
  Copy --> Loan

  class Loan{
    +copy: Copy
    +member: Member
    +checkoutDate: LocalDate
    +dueDate: LocalDate
    +returnedDate: LocalDate?
    +isOverdue(): boolean

    note for Loan "Invariant Properties:
    <ul>
    <li> copy != null 
    <Li> member != null
    <li> checkoutDate != null
    <li> dueDate != null
    <li> dueDate >= checkoutDate
    <li> returnedDate == null || returnedDate >= checkoutDate
    </ul>
  }

  class Hold{
    +media: Media
    +member: Member
    +placedAt: Instant

    note for Hold "Invariant Properties:
    <ul>
    <li>media != null && member != null
    <li> placedAt != null
    </ul>
  }

  class Resource{
    <<abstract>>
    +id: UUID
    +name: String
    +description: String
    +reviews: List<Review>

    note for Resource "Invariant Properties:
    <ul> id != null
    <li> name != null && name.length > 0
    <li> description != null
    </ul>"
  }
  Resource --> Reviewable
  Resource  --> Review

  class RoomResource{
    +capacity: int

    note for RoomResource "Invariant Properties:
    <ul>
    <li> capacity >= 0
    </ul>"
  }

  class EquipmentResource{
    +type: String

    note for EquipmentResource "Invariant Properties:
    <ul>
    <li> type != null && type.length > 0

    </ul>
  }
  Resource --> RoomResource
  Resource --> EquipmentResource

  class Booking{
    +id: UUID
    +resource: Resource
    +member: Member
    +start: Instant
    +end: Instant
    +overlaps(other: Booking): boolean

    note for Booking "Invariant Properties:
    <ul>
    <li> id != null;
    <li> resource != null;
    <li> member != null
    <li> start
    <li> end != null
    <li> end >= start
    </ul>"
  }
    Booking --> Resource
    Booking --> Member

  class Review{
    +id: UUID
    +author: Member
    +rating: int
    +text: String
    +createdAt: Instant

    note for Review "Invariant Properties:
    <ul>
    <li> id != null && author != null
    <li> createdAt != null
    <li> text != null && text.length > 0
    <li> 1 <= rating <= 5
    </ul>"
  }

  class Reviewable{
    <<interface>>
    +addReview(r: Review)
    +getReviews(): List<Review>

  }

  class FloorMap{
    +width: int
    +height: int
    +cells: Cell[]
    +legend: Map<CellType, String>
    +cellAt(x:int, y:int): Cell

    note for FloorMap "Invariant Properties:
    <ul>
    <li> width > 0 && height > 0
    <li> cells.length == width * height
    </ul>
  }
  FloorMap --* Cell

  class Cell{
    +x: int
    +y: int
    +label: String
    +type: CellType

    note for Cell "Invariant Properties:
    <ul>
    <li> 0 <= x < FloorMap.width
    <li> 0 <= y < FloorMap.height
    <li> type != null
    <li> label != null 
  }
  Cell  --> CellType

  %% ====== Enumerations (implicit invariants: value ∈ set) ======
  class ContactType{
    <<enumeration>>
    EMAIL
    PHONE
    ADDRESS


  }

  class AccountStatus{
    <<enumeration>>
    ACTIVE
    SUSPENDED
    BORROWING_BLOCKED

  }

  class MediaCategory{
    <<enumeration>>
    CHILDREN
    FANTASY
    SCIENCE
    HISTORY
    OTHER

  }

  class CopyStatus{
    <<enumeration>>
    AVAILABLE
    ON_LOAN
    LOST
    REPAIR

  }

  class CellType{
    <<enumeration>>
    EMPTY
    SHELVES_CHILDREN
    SHELVES_FANTASY
    SHELVES_SCIENCE
    COMPUTER_AREA
    ROOM
    DESK
    ENTRANCE
    WALL
    CORRIDOR
  }
