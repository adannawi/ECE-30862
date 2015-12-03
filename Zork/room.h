#ifndef ROOM_H
#define ROOM_H
#include <vector>
#include <string>
#include <iostream>
#include "ZorkObject.h"

class Room : public ZorkObject {
 private:
    std::vector<Room> rooms;
    std::string title;
    std::string description;
 public:
    Room(std::string tit, std::string desc);
    Room();
    void addExit(Room room, char direction);
    void setTitle(std::string tit);
    void setDescription(std::string desc);
    void printRoom();
};
#endif
