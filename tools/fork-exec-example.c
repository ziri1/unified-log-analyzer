#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main()
{
    char wd[2048];
    pid_t pid = -1;
    char *const ls_args[2] = {"-l", NULL};

    pid = fork();
    if (pid == 0)
    {
        if (getcwd((char *)&wd, (size_t)2048) == NULL)
        {
            fprintf(stderr, "Buffer too small.\n");

            exit(EXIT_FAILURE);
        }
        printf("Child's working directory: %s\n", wd);

        execvp("ls", ls_args);
        printf("execvp(): Executing process failed.\n");
    }
    else
    {
        printf("Child's PID: %d\n", pid);
    }

    exit(EXIT_SUCCESS);
}

/* vim: tabstop=4 shiftwidth=4 expandtab filetype=c
 */
