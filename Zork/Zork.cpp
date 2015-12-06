#include <iostream>
#include <sstream>
#include <iterator>
#include <vector>

#include "Zork.h"
#include "Item.h"
#include "Container.h"
#include "Room.h"
#include "Creature.h"

Zork::Zork(string filename){
    try{
	createGame(filename);
    }catch(int error){
	std::cout << "Something went wrong while loading the XML file." << std::endl;
    }
}

Zork::~Zork(){
}

Zork::Zork(Zork& master){
}

bool Zork::createGame(string filename){
    xml_document<> game;
    xml_node<> * root_node;

    //Load XML file
    ifstream zorkFile(filename.c_str());
    if (!zorkFile.is_open()){
	cout << "Error opening file" << endl;
	return false;
    }
    //Do some magic
    vector<char> buffer((istreambuf_iterator<char>(zorkFile)), istreambuf_iterator<char>());
    buffer.push_back('\0');
    game.parse<0>(&buffer[0]);
    
    //Set root node
    root_node = game.first_node();
    
    //Initiate first node 
    xml_node<> * node = root_node -> first_node();

    list<xml_node<>*> rooms_list;
    list<xml_node<>*> items_list;
    list<xml_node<>*> containers_list;
    list<xml_node<>*> creatures_list;

    //Push rooms, items, containers, and creatures into respective lists
    while (node != NULL){
	if (string((node->name())) == string("room")){
	    rooms_list.push_front(node);
	    //  cout << "Room: " << node->first_node("name")->value() << endl; //Print out everything in rooms list
	}

	if (string((node->name())) == string("item")){
	    items_list.push_front(node);
	    // cout << "Item: " << node->first_node("name")->value() << endl; //Print items
	}

	if (string((node->name())) == string("container")){
	    containers_list.push_front(node);
	    // cout << "Container: " << node->first_node("name")->value() << endl; //Print containers
	}

	if (string((node->name())) == string("creatures")){
	    creatures_list.push_front(node);
	    // cout << "Creature: " << node->first_node("name")->value() << endl; //Print creatures
	}
	node = node -> next_sibling();
    }


    //Create Item Objects
    Item * newItem;
    for (list<xml_node<>*>::iterator it = items_list.begin(); it != items_list.end(); it++){
	newItem = new Item(*it);
	types[newItem->name] = "item";
	items[newItem->name] = newItem;
	objects[newItem->name] = newItem;
    }

    //Create Container Objects
    Container * newContainer;
    for (list<xml_node<>*>::iterator it = containers_list.begin(); it != containers_list.end(); it++){
	newContainer = new Container(*it);
	types[newContainer->name] = "container";
	containers[newContainer->name] = newContainer;
	objects[newContainer->name] = newContainer;
    }

    //Create Room Objects
    Room * newRoom;
    for (list<xml_node<>*>::iterator it = rooms_list.begin(); it != rooms_list.end(); it++){
	newRoom = new Room(*it);
	types[newRoom->name] = "room";
	rooms[newRoom->name] = newRoom;
	objects[newRoom->name] = newRoom;
    }

    
    //Create creature objects
    Creature * newCreat;
    for (list<xml_node<>*>::iterator it = creatures_list.begin(); it != creatures_list.end(); it++){
	newCreat = new Creature(*it);
	types[newCreat->name] = "creature";
	creatures[newCreat->name] = newCreat;
	objects[newCreat->name] = newCreat;
    }
   

    completeFlag = true;
    return true;
}

void Zork::begin(){
    currRoom = "Entrance";
    bool triggerState = false;
    cout << rooms.find(currRoom)->second->description << endl;
    while (true){
	getline(cin, userIn);
	triggerState = false;
	triggerState = checkTriggers();
	if (triggerState){ continue; }
	if (userIn == ""){ cout << "Error!" << endl; continue; }
	checkCommand(userIn);
	userIn = "";
      	checkTriggers();
    }
}

    bool Zork::checkTriggers(){
	bool state = false;
	Room * roomPtr = rooms.find(currRoom)->second;
	Container * tempCont;
	Item * tempItem;
	Creature * tempCreat;
	Trigger * tempTrig;

	//Check triggers in room
	for (list<Trigger*>::iterator trig_it = roomPtr->triggers.begin(); trig_it != roomPtr->triggers.end();){
	    tempTrig = *trig_it;
	    if (tempTrig->evaluate(*this)){
		cout << tempTrig->print << endl;
		completeAction(tempTrig->action);
		if (tempTrig->hasCommand){
		    state = true;
		}
		if (tempTrig->type == "single"){
		    trig_it = roomPtr->triggers.erase(trig_it);
		    delete tempTrig;
		}
		else{
		    ++trig_it;
		}
	    }
	    else{
		++trig_it;
	    }
	}
	//Check items inside containers, then check containers
	for (map<string, string>::iterator container_it = roomPtr->containers.begin();
	     container_it != roomPtr->containers.end(); ++container_it){

	    tempCont = containers.find(container_it->second)->second;

	    for (map<string, string>::iterator item_it = tempCont->items.begin();
		 item_it != tempCont->items.end(); ++item_it){

		tempItem = items.find(item_it->second)->second;

		for(list<Trigger*>::iterator trig_it = tempItem->triggers.begin();
		    trig_it != tempItem->triggers.end();){

		    tempTrig = *trig_it;
		    if (tempTrig->evaluate(*this)){
			cout << tempTrig->print << endl;
			completeAction(tempTrig->action);
			if (tempTrig->hasCommand){
			    state = true;
			}
			if (tempTrig->type == "single"){
			    trig_it = tempItem->triggers.erase(trig_it);
			    delete tempTrig;
			}else{
			    ++trig_it;
			}
		    }else{
			++trig_it;
		    }
		}
	    }

	    //Check container tirggers
	    for (list<Trigger*>::iterator trig_it = tempCont->triggers.begin(); trig_it != tempCont->triggers.end(); ){
		tempTrig = *trig_it;
		if(tempTrig->evaluate(*this)){
		    cout << tempTrig->print;
		    completeAction(tempTrig->action);
		    if (tempTrig->hasCommand){
			state = true;
		    }
		    if (tempTrig->type == "single"){
			trig_it = tempCont->triggers.erase(trig_it);
			delete tempTrig;
		    }else{
			++trig_it;
		    }
		}else{
		    ++trig_it;
		}
	    }
	}
	//Check creatures
	for (map<string, string>::iterator creatures_it = roomPtr->creatures.begin(); creatures_it != roomPtr->creatures.end(); ++creatures_it){
	    tempCreat = creatures.find(creatures_it->second)->second;
	    for (list<Trigger*>::iterator trig_it = tempCreat->triggers.begin(); trig_it != tempCreat->triggers.end();){
		tempTrig = *trig_it;
		if(tempTrig->evaluate(*this)){
		    cout << tempTrig->print << endl;
		    completeAction(tempTrig->action);
		    if (tempTrig->hasCommand){
			state = true;
		    }
		    if (tempTrig->type == "single"){
			trig_it = tempCreat->triggers.erase(trig_it);
			delete tempTrig;
		    }else{
			++trig_it;
		    }
		}else{
		    ++trig_it;
		}
	    }
	}
	//Inventory check
	for (map<string,string>::iterator inv_it = inventory.begin(); inv_it != inventory.end(); ++inv_it){
	    tempItem = items.find(inv_it->second)->second;
	    for (list<Trigger*>::iterator trig_it = tempItem->triggers.begin(); trig_it != tempItem->triggers.end();){
		tempTrig = *trig_it;
		if (tempTrig -> evaluate(*this)){
		    cout << tempTrig -> print << endl;
		    completeAction(tempTrig->action);
		    if(tempTrig->hasCommand){
			state = true;
		    }
		    if(tempTrig->type == "single"){
			trig_it = tempItem->triggers.erase(trig_it);
			delete tempTrig;
		    }else{
			++trig_it;
		    }
		}else{
		    ++trig_it;
		}
	    }
	}

	//Items in room
	for (map<string, string>::iterator item_it = roomPtr->items.begin(); item_it != roomPtr->items.end(); ++item_it){
	    tempItem = items.find(item_it->second)->second;
	    for (list<Trigger*>::iterator trig_it = tempItem->triggers.begin(); trig_it != tempItem->triggers.end();){
		tempTrig = *trig_it;
		if (tempTrig->evaluate(*this)){
		    cout << tempTrig->print << endl;
		    completeAction(tempTrig->action);
		    if (tempTrig->hasCommand){
			state = true;
		    }
		    if (tempTrig->type == "single"){
			trig_it = tempItem->triggers.erase(trig_it);
			delete tempTrig;
		    }else{
			++trig_it;
		    }
		}else{
		    ++trig_it;
		}
	    }
	}

	return state;

    }

void Zork::checkCommand(string input){
    Room * roomPtr = rooms.find(currRoom)->second;
    istringstream buf(input);
    istream_iterator<string> beg(buf), end;
    vector<string> command(beg, end);

    //Handle room movement
    if (input == "n" || input == "e" || input == "w" || input == "s"){
	Room * currentRoom = rooms.find(currRoom)->second;
	string direction = "";
	string nextRoom = "";
	bool availablePath = false;
	if (input == "n") { direction = "north"; }
	if (input == "e") { direction = "east"; }
	if (input == "w") { direction = "west"; }
	if (input == "s") { direction = "south"; }
	availablePath = (currentRoom->borders.find(direction))!=(currentRoom->borders.end());
	if (availablePath){
	    nextRoom = currentRoom->borders.find(direction)->second;
	    currRoom = nextRoom;
	    cout << rooms.find(currRoom)->second->description << endl;
	}else{
	    cout << "Can't go that way!" << endl;
	}
    }

    //Handle inventory print
    else if (input == "i"){
	if (inventory.empty()){
	    cout << "Inventory: empty" << endl;
	}else{
	    for (map<string, string>::iterator it = inventory.begin(); it != inventory.end(); ++it){
		if (it == inventory.begin()){
		    cout << "Inventory: " << it->second;
		}else{
		    cout << ", " << it->second;
		}
	    }
	    cout << "." << endl;
	}
    }
    else if (input == "open exit"){
	if (rooms.find(currRoom)->second->type == "exit"){
	    cout << "Game over! Woo!" << endl;
	    exit(EXIT_SUCCESS);
	}else{
	    cout << "Error!" << endl;
	}
    }
    else if (command[0] == "take" && command.size() > 1){
	Room * roomPtr = rooms.find(currRoom)->second;
	Container * tempCont;

	if (roomPtr->items.find(command[1]) != roomPtr->items.end()){
	    inventory[command[1]] = command[1];
	    roomPtr->items.erase(command[1]);
	    cout << "Item: " << command[1] << " added to inventory!" << endl;
	}
	else{
	    bool checkComplete = false;
	    for (map<string,string>::iterator it = roomPtr->containers.begin(); it != roomPtr->containers.end(); ++it){
		tempCont = containers.find(it->second)->second;
		if (tempCont != NULL && tempCont->openState && tempCont->items.find(command[1]) != tempCont->items.end() && !checkComplete){
		    inventory[command[1]] = command[1];
		    tempCont->items.erase(command[1]);
		    cout << "Item: " << command[1] << " added to inventory!" << endl;
		    checkComplete = true;
		}
	    }
	    if (!checkComplete){
		cout << "Error" << endl;
	    }
	}
    }
    else if (command[0] == "open" && command.size() > 1){
	Room * roomPtr = rooms.find(currRoom)->second;
	Container * tempCont;
	bool isContainerHere;
	isContainerHere = ((roomPtr->containers.find(command[1]) !=
			    roomPtr->containers.end()));
	if (isContainerHere){
	    tempCont = containers.find(command[1])->second;
	    tempCont->openState = true;
	    if (tempCont->items.empty()){
		cout << command[1] << " is empty! Go away!" << endl;
	    }else{
		for (map<string, string>::iterator it = tempCont->items.begin(); it != tempCont->items.end(); ++it){
		    if (it == tempCont->items.begin()){
			cout << command[1] << " contains " << it->second;
		    }else{
			cout << ", " << it->second;
		    }	
		    //print out container contents
		}
		cout <<". " << endl;
	    }
	}else{
	    cout << "Error!" << endl;
	}
    }
      
    
    else if (command[0] == "read" && command.size() > 1){
	Item * itemptr;
	if (inventory.find(command[1]) != inventory.end()){
	    itemptr = items.find(command[1])->second;
	    if (itemptr->writing != ""){
		cout << itemptr -> writing << endl;
	    }else{
		cout << "Nothing interesting 'bout this.." << endl;
	    }
	}else{
	    cout << command[1] << " is not in inventory!" << endl;
	}
    }
    else if (command[0] == "drop" && command.size() > 1){
	Room * roomPtr = rooms.find(currRoom)->second;
	if (inventory.find(command[1]) != inventory.end()){
	    roomPtr->items[command[1]] = command[1];
	    inventory.erase(command[1]);
	    cout << command[1] << " dropped.." << endl;
	}else{
	    cout << command[1] << " is not in inventory!" << endl;
	}
    }
    else if (command[0] == "attack" && command.size() > 1){
	//attack it
    }
    else if (command[0] == "put" && command.size() > 1){
	//put it
    }
    else if (command.size() > 2 && command[0] == "turn" && command[1] == "on"){
	Room * roomptr = rooms.find(currRoom)->second;
	Item * itemptr;
	if (inventory.find(command[2]) != inventory.end()){
	    itemptr = items.find(command[2])->second;
	    cout << command[2] << "has been activated!" << endl;
	
	cout << itemptr->turnOnMessage << endl;
	completeAction(itemptr->turnOnAction);
	}else{
	    cout << "Error!" << endl;
	}
    }else{
	cout << "Error!" << endl;
    }
}

void Zork::completeAction(string action){
    istringstream buf(action);
    istream_iterator<string>beg(buf), end;
    vector<string> splitAction(beg, end);
    //HANDLE UPDATE ACTIONS
    if (splitAction[0] == "Update"){
	string object = splitAction[1];
	string newStatus = splitAction[3];
	string objType = types.find(object)->second;
	if(objType == "room"){
	    rooms.find(object)->second->status = newStatus;
	}
	if(objType == "item"){
	    items.find(object)->second->status = newStatus;
	}
	if(objType == "container"){
	    containers.find(object)->second->status = newStatus;
	}
	if (objType == "creature"){
	    creatures.find(object)->second->status = newStatus;
	}
    }
    //HANDLE ADD ACTIONS
    if (splitAction[0] == "Add"){
	string resultLocation = splitAction[3];
	string object = splitAction[1];
	string objType = types.find(object)->second;
	string resultLocationType = types.find(resultLocationType)->second;

	if (resultLocationType == "container"){
	    Container * resultContainer = containers.find(resultLocation)->second;
	    if (objType == "item"){
		resultContainer->items[object] = object;
	    }else{
		cout << "Error" << endl;
	    }
	}
	else if (resultLocationType == "room"){
	    Room * resultRoom = rooms.find(resultLocation)->second;
	    if (objType == "creature"){
		resultRoom->creatures[object] = object;
	    }
	    else if (objType == "container"){
		resultRoom->containers[object] = object;
	    }
	    else if (objType == "item"){
		resultRoom->items[object] = object;
	    }else{
		cout << "Error!" << endl;
	    }  
	}
	else{
	    cout << "Error!" << endl;
	}
    }

    else  if (splitAction[0] == "Delete"){
	string objType = types.find(splitAction[1]->second);
	if (objType == "room"){

    }
    else if (splitAction.size()>1 && splitAction[0] == "Game" && splitAction[1] == "Over"){
    }
    else{
	userIn = action;
	checkCommand(action);
    }
}
    
