#!/bin/sh
#
# Unix Shell Script for CDL to BOOLEAN conversion v0.1, April 6 2011
#
# Written by Arnaud Hubaux <ahubaux@gmail.com>
#
# USAGE
# sh cdl2bool <cdl_input_file> [<bool_output_file>]
#
#
# EXAMPLES
# sh cdl2bool ~/cdl_samples/cdl_file 
# This command takes the "~/cdl_samples/cdl_file" file as input and outputs the converted files "~/cdl_samples/cdl_file.bool". 
#
# sh fcdl2bool ~/cdl_samples/cdl_file ~/bool_samples/bool_file
# This command takes the "~/cdl_samples/cdl_file" file as input and outputs the converted files "~/bool_samples/bool_file". 

# PROCESSING

# Argument processing
if [ $# -eq 1 ]
	then
	OUTPUT_FILE=${1%\.*}".bool"
elif [ $# -eq 2 ]
	then
	OUTPUT_FILE=$2
else
	echo "Error in $0 - Invalid Argument Count"
    echo "Syntax: $0 <input_file> [<output_file>]"
   	exit
fi

# MAIN
echo "Starting IML to BOOL conversion of $1"
rm -rf $OUTPUT_FILE
mvn scala:run -q -DmainClass=gsd.cdl.Iml2BoolMain -DaddArgs="$1|$OUTPUT_FILE"


