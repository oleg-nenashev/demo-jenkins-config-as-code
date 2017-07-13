import jenkins.model.Jenkins;

boolean allowRunsOnMaster = Boolean.getBoolean("io.jenkins.dev.security.allowRunsOnMaster");;

println("--- System configuration")

// TODO: Job Restrictions
if (allowRunsOnMaster) {
    println("Runs on Master are enabled. It is a bad idea from the security standpoint")
    //TODO: Should be another option when Job Restrictions are introduced.
}

// TODO: Configure Job Restrictions, Script Security, Authorize Project, etc., etc.
