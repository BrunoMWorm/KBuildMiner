#!/bin/sh
#
# Unix Shell Script for BOOLEAN to DIMACS conversion v0.1, April 6 2011
#
# Written by Arnaud Hubaux <ahubaux@gmail.com>
#
# USAGE
# sh bool2dimacs <bool_input_file> [<dimacs_output_file>]
#
#
# EXAMPLES
# sh bool2dimacs ~/bool_samples/bool_file 
# This command takes the "~/bool_samples/bool_file" file as input and outputs the converted files "~/bool_samples/bool_file.dimacs". 
#
# sh bool2dimacs ~/bool_samples/bool_file ~/dimacs_samples/dimacs_file
# This command takes the "~/bool_samples/cdl_file" file as input and outputs the converted files "~/dimacs_samples/dimacs_file". 

# PROCESSING

# Argument processing
if [ $# -eq 1 ]
	then
	OUTPUT_FILE=${1%\.*}".dimacs"
elif [ $# -eq 2 ]
	then
	OUTPUT_FILE=$2
else
	echo "Error in $0 - Invalid Argument Count"
    echo "Syntax: $0 <input_file> [<output_file>]"
   	exit
fi

# MAIN
echo "Starting BOOL to DIMACS conversion of $1"
rm -rf $OUTPUT_FILE
mvn scala:run -q -DmainClass=gsd.linux.tools.CNFMain -DaddArgs="$1|$OUTPUT_FILE"


