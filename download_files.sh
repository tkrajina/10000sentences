mkdir -p bucket_files
mkdir -p raw_files
cd raw_files

wget http://opus.nlpl.eu/download.php?f=OpenSubtitles2018%2Fen-sl.tmx.gz -O en-sl.tmx.gz
gzip -d en-sl.tmx.gz

wget http://www.statmt.org/europarl/v7/et-en.tgz
gzip -d et-en.tgz
tar -xvf et-en.tar

wget http://www.statmt.org/europarl/v7/lv-en.tgz
gzip -d lv-en.tgz
tar -xvf lv-en.tar

wget http://www.statmt.org/europarl/v7/sk-en.tgz
gzip -d sk-en.tgz
tar -xvf sk-en.tar

wget http://downloads.tatoeba.org/exports/sentences.tar.bz2
bzip2 -d sentences.tar.bz2
tar -xvf sentences.tar

wget http://downloads.tatoeba.org/exports/sentences_detailed.tar.bz2
bzip2 -d sentences_detailed.tar.bz2
tar -xvf sentences_detailed.tar


