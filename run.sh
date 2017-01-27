#!/bin/bash

export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/opt/rti_connext_dds-5.2.3/lib/i86Linux2.6gcc4.4.5"
export RTI_LICENSE_FILE=/opt/rti_connext_dds-5.2.3/rti_license.dat

java -jar dist/ddsWrapper.jar
