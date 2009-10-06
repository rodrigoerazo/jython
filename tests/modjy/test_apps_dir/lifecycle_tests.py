# -*- coding: windows-1252 -*-

###
#
# Copyright Alan Kennedy. 
# 
# You may contact the copyright holder at this uri:
# 
# http://www.xhaus.com/contact/modjy
# 
# The licence under which this code is released is the Apache License v2.0.
# 
# The terms and conditions of this license are listed in a file contained
# in the distribution that also contained this file, under the name
# LICENSE.txt.
# 
# You may also read a copy of the license at the following web address.
# 
# http://modjy.xhaus.com/LICENSE.txt
#
###

import atexit

def exit_func():
    import java
    java.lang.System.setProperty("modjy", "gone")

atexit.register(exit_func)

def lifecycle_test(environ, start_response):
    writer = start_response("200 OK", [])
    writer("Hello World!")
    return []
