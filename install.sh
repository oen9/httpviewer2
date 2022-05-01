#!/bin/bash
sudo cp ./jvm/target/universal/stage /usr/local/share/httpviewer2/ -R
sudo ln -s /usr/local/share/httpviewer2/bin/httpviewer2 /usr/local/bin/

sudo chgrp users /usr/local/share/httpviewer2/bin/httpviewer2
sudo chmod g+x /usr/local/share/httpviewer2/bin/httpviewer2
