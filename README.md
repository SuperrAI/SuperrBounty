# Superr Bounty Template

## Project Configuration

All library and package versions have been frozen in gradle files. Listing down the major ones
below.

> These should not be changed. Gradle files changes should be avoided at all costs unless adding a
> new library.

1. Java version: 17 (source and target compatibility)
2. Kotlin version: 1.9.23
3. Gradle version: 8.7
4. Kotlin-Gradle Plugin version: 1.9.23
5. Android:
    - Min: 29
    - Target: 31
    - Compile: 34
6. Compose BOM: 2024.09.00

## Directory and their roles

Listing down the high level functions of the major directories and project structure inside
app/src/main or app/src/main/java/com/superr/bounty

### res/drawable

Contains all Vector drawables imported from Figma designs. All icons should be rendered as Vector
drawables.

### res/font

Contains all fonts used in the designs

### res/values/strings.xml

Contains all necessary string copies.

### activities/

Contains all immersive experience activities that need to run standalone

### data/dao/

Folder to hold all Data access objects for RoomDB

### data/dto/

Folder to hold all Data Transfer objects

### data/mapper/

Folder to hold logic that can map domain to DTO and vice versa

### data/repository/

Folder contains all repository accessors

### domain/

Contains all core logic classes

### ui/common/

Contains all Composables common across multiple screens

### ui/navigation/

Contains all navigation related UI components

### ui/theme/

Contains the centralized theme logic

### ui/view/

Contains all screens examples