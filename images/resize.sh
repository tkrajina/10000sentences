for i in $(ls *png)
do
    convert $i -resize 500x500 $i
done
