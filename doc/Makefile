DIAGRAMS = \
    basic-idea.dia

IMAGES = $(DIAGRAMS:.dia=.png)

%.png: %.dia
	dia --export='$@' --filter=cairo-alpha-png $<


all: $(IMAGES)

clean:
	rm -f $(IMAGES)

.PHONY: all clean
