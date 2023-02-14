# Files organizer

[![License](https://img.shields.io/github/license/adamjak/files-organizer)](https://github.com/adamjak/files-organizer/blob/master/LICENSE)
![Contributors](https://img.shields.io/github/all-contributors/adamjak/files-organizer)
![Last commit](https://img.shields.io/github/last-commit/adamjak/files-organizer)



## Features

Simple util which copying or moving files that it sorts into folders according to their creation date.  


## Install

### Unix 

```shell
git  clone https://github.com/adamjak/files-organizer.git
cd files-organizer
./gradlew clean build
unzip  build/distributions/files_organizer-[CURRENT_VERSION].zip -d /opt/
sudo ln -s /opt/files_organizer-[CURRENT_VERSION]/bin/files_organizer /usr/local/bin/
```

## Usage
```
-i,--input-folder <arg>    Input folder
-m,--move                  Files will be moved not copied.
-o,--output-folder <arg>   Output folder
-r,--replace               Replace existing file with copied file.
-t,--tree                  Organise file on tree structure like year folder, 
                           month folder and day folder.
```


## Examples
```shell
$ files_organizer -i /media/user/USB/photos -o ~/pictures/ -m -t
```
This will move all files from `/media/user/USB/photos` to `~/pictures` and create tree directory structure which start with year folder, next continue month folder and last will be day folder in which will be all files with create date whitch represent tree structure of folder names. 


## Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->



