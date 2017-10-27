#!/bin/sh

# Installation of a compiled JQEASSEMBLER.
# All this does is edit the "JQEASSEMBLER_DIR=..." lines in the program startup scripts
# so if you have any problems, just do this manually
# NB: This installation script should be run from within the JQEASSEMBLER directory

# You are supposed to run this from the main JQEASSEMBLER directory
# but in case someone is in the bin directory, change...

JQEASSEMBLER_DIR=`pwd`
if [ `basename "$JQEASSEMBLER_DIR"` = bin ]; then
  JQEASSEMBLER_DIR=`cd ..;pwd`
fi

# Now start the 'installation'
if [ ! "$1" = "silent" ] ; then
    echo "Installing JQEASSEMBLER (directory=$JQEASSEMBLER_DIR)"
fi
TEMP_FILE=tmp
FILES_TO_CHANGE=`find bin -maxdepth 1 ! -type d ! -name '*.bat'`
for FILE_TO_CHANGE in $FILES_TO_CHANGE
do
  if [ -f "$JQEASSEMBLER_DIR"/$FILE_TO_CHANGE ]; then
    if [ ! "$1" = "silent" ] ; then
        echo "Setting path in startup script $JQEASSEMBLER_DIR/$FILE_TO_CHANGE..."
    fi
    if sed -e "s|JQEASSEMBLER_DIR=.*|JQEASSEMBLER_DIR=$JQEASSEMBLER_DIR|g" "$JQEASSEMBLER_DIR"/$FILE_TO_CHANGE > "$JQEASSEMBLER_DIR"/$TEMP_FILE ; then
      /bin/mv "$JQEASSEMBLER_DIR"/$TEMP_FILE "$JQEASSEMBLER_DIR"/$FILE_TO_CHANGE
      chmod 755 "$JQEASSEMBLER_DIR"/$FILE_TO_CHANGE
    else
      echo "Error: Failed to modify startup scripts."
      exit 0
    fi
  else
    echo "Error: Could not locate startup script $JQEASSEMBLER_DIR/$FILE_TO_CHANGE"
    exit
  fi
done
if [ ! "$1" = "silent" ] ; then
    echo "Installation complete."
fi
