mkdir -p bucket_files
mkdir -p raw_files
cd raw_files

wget -c http://opus.nlpl.eu/download.php?f=OpenSubtitles2018%2Fen-sl.tmx.gz -O en-sl.tmx.gz
gzip -d en-sl.tmx.gz

wget -c http://opus.nlpl.eu/download.php?f=OpenSubtitles2018%2Fen-et.tmx.gz -O en-et.tmx.gz
gzip -d en-et.tmx.gz

wget -c http://opus.nlpl.eu/download.php?f=OpenSubtitles2018%2Fen-lv.tmx.gz -O en-lv.tmx.gz
gzip -d en-lv.tmx.gz

wget -c http://opus.nlpl.eu/download.php?f=OpenSubtitles2018%2Fen-sk.tmx.gz -O en-sk.tmx.gz
gzip -d en-sk.tmx.gz

wget -c http://opus.nlpl.eu/download.php?f=OpenSubtitles2018%2Fen-hr.tmx.gz -O en-hr.tmx.gz
gzip -d en-hr.tmx.gz

wget -c http://opus.nlpl.eu/download.php?f=OpenSubtitles2018%2Fen-sq.tmx.gz -O en-sq.tmx.gz
gzip -d en-sq.tmx.gz

wget -c http://downloads.tatoeba.org/exports/links.tar.bz2
bzip2 -d links.tar.bz2
tar -xvf links.tar

wget -c http://downloads.tatoeba.org/exports/sentences.tar.bz2
bzip2 -d sentences.tar.bz2
tar -xvf sentences.tar

wget -c http://downloads.tatoeba.org/exports/sentences_detailed.tar.bz2
bzip2 -d sentences_detailed.tar.bz2
tar -xvf sentences_detailed.tar


