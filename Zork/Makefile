
#------------------------------------------------------------------------------

SOURCE=Command.cpp Container.cpp Creature.cpp HasCondition.cpp Item.cpp main.cpp Room.cpp StatusCondition.cpp Trigger.cpp Zork.cpp ZorkObject.cpp
MYPROGRAM=Zork


CC=g++

FLAGS= -w -std=c++0x

#------------------------------------------------------------------------------



all: $(MYPROGRAM)



$(MYPROGRAM): $(SOURCE)

	$(CC) $(FLAGS) $(SOURCE) -o$(MYPROGRAM) 

clean:

	rm -f $(MYPROGRAM)
