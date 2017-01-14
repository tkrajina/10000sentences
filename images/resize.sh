for i in $(ls *png)
do
    convert $i -resize 400x400 $i
done
