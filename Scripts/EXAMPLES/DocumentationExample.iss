#  DocumentationExample.iss
#
#  @overview  The purpose of this operator is to demostrate how 
#             documentation inside of an operator should 
#             be done.  Each part of the documentation system is included 
#             in at least a demonstrative fasion.  The documentation listed
#             here will appear in the Help System for this script.
#
#  $Date$
#
#  @assumptions   Not too many in a script like this
#
#  @algorithm Well Silly there is nothing really being done here so how 
#             could there be an algorithm
#
#  @authors    John Hammonds<JPHammonds@anl.gov
#
#  @param     test An integer
#
#  @return    Input integer + 5
#
#  @error     At present no error handling done here.

$Category = Macros, Examples, Scripts ( ISAW )

$test    Integer     Enter a number

result = test + 5
return result
