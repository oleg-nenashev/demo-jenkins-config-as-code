println """
###############################
# boot - Master Hook (start)  #
###############################
"""


println("== Configuring Master computer")

// Admin owns the node
//NodeOwnerHelper.setOwnership(Jenkins.getInstanceOrNull(), new OwnershipDescription(true, "admin"))

println """
###############################
# boot - Master Hook (end)    #
###############################
"""
