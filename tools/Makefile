SRCS = $(shell find -type f -name '*.c')
TARGETS = $(SRCS:.c=)

.DEFAULT: all

all: $(TARGETS)

clean:
	rm -f $(TARGETS)

.PHONY: all clean
