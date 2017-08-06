mkdir -p raw_files
cd raw_files

wget http://www.statmt.org/europarl/v7/sl-en.tgz
gzip -d sl-en.tgz
tar -xvf sl-en.tar

wget http://downloads.tatoeba.org/exports/sentences.tar.bz2
bzip2 -d sentences.tar.bz2
tar -xvf sentences.tar

wget http://downloads.tatoeba.org/exports/sentences_detailed.tar.bz2
bzip2 -d sentences_detailed.tar.bz2
tar -xvf sentences_detailed.tar
