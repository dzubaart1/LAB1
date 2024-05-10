package list;

public class Set
{
    public int Start, End; // Начало и Старт множетсва

    private Item _end; // конец списка

    // Инициализирующий конструктор
    public Set(int start, int end)
    {
        Start = Math.min(start, end);
        End = Math.max(start, end);
        _end = null;
    }

    // Копирующий конструктор
    public Set(Set b)
    {
        CopySets(this, b);
    }

    // Вставка нового эл-та
    public void Insert(int x)
    {
        // Если эл-т вне диапазона, то return
        if(!IsInRange(x))
        {
            return;
        }

        // Создаем новый эл-т
        Item newItem = new Item(x, null);

        // Если Set пуст, то инициализируем _end
        if(_end == null)
        {
            _end = newItem;
            newItem.Next = _end;
            return;
        }

        // Ищем первый эл-т меньший значения
        Item prevItem = GetFirstLessItem(x);

        // Если такого эл-та нет, то давбялем в конец
        if(prevItem == null)
        {
            _end = AppendItem(_end, x);
            return;
        }

        // Если такой эл-т уже есть, то return
        if(prevItem.Next.Value == x)
        {
            return;
        }

        // Добавляем восле предыдущего
        Item temp = prevItem.Next;
        newItem.Next = temp;
        prevItem.Next = newItem;
    }

    // Проверяет наличие эл-та в множестве
    public boolean Member(int x)
    {
        // Если эл-т вне диапазона, то return
        if(!IsInRange(x))
        {
            return false;
        }

        // Ищем первый эл-т меньший значения
        Item prevItem = GetFirstLessItem(x);

        // Если его нет, то false
        if(prevItem == null)
        {
            return false;
        }

        // Если есть, то проверяем равенство следующего со значением
        return prevItem.Next.Value == x;
    }

    // Удаляет эл-т из множества
    public void Delete(int x)
    {
        // Если эл-т вне диапазона, то return
        if(!IsInRange(x))
        {
            return;
        }

        // Ищем первый эл-т меньший значения
        Item prevItem = GetFirstLessItem(x);

        // Если его нет, то return
        if(prevItem == null)
        {
            return;
        }

        // Если есть, то проверяем равенство следующего со значением, если не равны то return
        if(prevItem.Next.Value != x)
        {
            return;
        }

        // Если у нас один эл-т в множестве, то обнуляем множество
        if(_end.Next == _end)
        {
            _end = null;
            return;
        }

        // Если удаляем последний эл-т
        if(prevItem.Next == _end)
        {
            Item temp = _end.Next;
            _end = prevItem;
            prevItem.Next = temp;
            return;
        }

        // Если удаляем из середины
        prevItem.Next = prevItem.Next.Next;
    }

    // Возвращает минимум из множества
    public int Min()
    {
        if(_end == null)
        {
            throw new RuntimeException("Empty set");
        }

        return _end.Next.Value;
    }

    // Возвращает максимум из множества
    public int Max()
    {
        if(_end == null)
        {
            throw new RuntimeException("Empty set");
        }

        return _end.Value;
    }

    // Копирует множество b в текущее
    public void Assign(Set b)
    {
        if(b == null)
        {
            return;
        }

        if(b == this)
        {
            return;
        }

        CopySets(this, b);
    }

    // Сравнивает два множества на равенсто
    public boolean Equal(Set b)
    {
        if(b == this)
        {
            return true;
        }

        if(b == null)
        {
            return false;
        }

        // Если оба множества пусты
        if(b._end == null)
        {
            return _end == null;
        }

        // Сравниваем поэлементно
        Item aCurrentItem = _end.Next;
        Item bCurrentItem = b._end.Next;

        while (aCurrentItem != _end && bCurrentItem != b._end)
        {
            if(aCurrentItem.Value != bCurrentItem.Value)
            {
                return false;
            }
        }

        // Сравниваем концы
        return _end.Value == b._end.Value;
    }

    // Находит в каком множества находится x
    public Set Find(Set b, int x)
    {
        // Ищем эл-т с GetFirstLessItem
        Item first = GetFirstLessItem(x);

        if(first == null)
        {
            return null;
        }

        if(first.Next.Value == x)
        {
            return this;
        }

        // Ищем эл-т с GetFirstLessItem
        first = b.GetFirstLessItem(x);

        if(first == null)
        {
            return null;
        }

        if(first.Next.Value == x)
        {
            return b;
        }

        return null;
    }

    // Объединеяет непересекающиеся множетсва
    public Set Merge(Set b)
    {
        if(b == null)
        {
            return new Set(b);
        }

        return Union(b);
    }

    // Обнуляет множество
    public void MakeNull()
    {
        _end = null;
    }

    // Выводит множество на экран
    public void Print()
    {
        if(_end == null)
        {
            System.out.println("Empty set!");
            return;
        }

        Item currentItem = _end.Next;

        int i = 0;
        while (currentItem != _end)
        {
            System.out.println(++i + ". " + currentItem.Value);
            currentItem = currentItem.Next;
        }

        System.out.println(++i + ". " + currentItem.Value);
    }

    // Пустое ли множество
    public boolean IsEmpty()
    {
        return _end == null;
    }

    // Пересекает два множества
    public Set Intersection(Set b)
    {
        if(b == null)
        {
            return null;
        }

        if(b == this)
        {
            return new Set(b);
        }

        // Если не пересекаются по интервалам, то null
        if(End <= b.Start || Start >= b.End)
        {
            return null;
        }

        // Пересекаем множества
        Item endItem = IntersectList(b);
        Set newSet = new Set(Math.max(Start, b.Start), Math.min(End, b.End));
        newSet._end = endItem;

        return newSet;
    }

    public boolean IsIntersectWith(Set b)
    {
        Item newList = IntersectList(b);

        if(newList != null)
        {
            return true;
        }

        return false;
    }

    // Пересечение множеств
    private Item IntersectList(Set b)
    {
        Item resItem = null;
        Item thisListIndex = _end.Next;
        Item bListIndex = b._end.Next;

        while (thisListIndex != _end && bListIndex != b._end)
        {
            if(thisListIndex.Value < bListIndex.Value)
            {
                thisListIndex = thisListIndex.Next;
            }
            else if(thisListIndex.Value > bListIndex.Value)
            {
                bListIndex = bListIndex.Next;
            }
            else
            {
                resItem = new Item(thisListIndex.Value, null);
                resItem.Next = resItem;

                thisListIndex = thisListIndex.Next;
                bListIndex = bListIndex.Next;
                break;
            }
        }

        if(resItem == null)
        {
            if(thisListIndex.Value == bListIndex.Value)
            {
                resItem = new Item(thisListIndex.Value, null);
                resItem.Next = resItem;
            }

            return resItem;
        }

        while (thisListIndex != _end && bListIndex != b._end)
        {
            if(thisListIndex.Value < bListIndex.Value)
            {
                thisListIndex = thisListIndex.Next;
            }
            else if(thisListIndex.Value > bListIndex.Value)
            {
                bListIndex = bListIndex.Next;
            }
            else
            {
                resItem = AppendItem(resItem, thisListIndex.Value);

                thisListIndex = thisListIndex.Next;
                bListIndex = bListIndex.Next;
            }
        }

        Item endItem = null;
        Item remainsItem = null;
        Item remainsEnd = null;

        if(thisListIndex == _end && bListIndex == b._end)
        {
            if(thisListIndex.Value == bListIndex.Value)
            {
                resItem = AppendItem(resItem, thisListIndex.Value);
            }
            return resItem;
        }

        else if (thisListIndex == _end)
        {
            endItem = thisListIndex;
            remainsItem = bListIndex;
            remainsEnd = b._end;
        }
        else
        {
            endItem = bListIndex;
            remainsItem = thisListIndex;
            remainsEnd = _end;
        }

        while (remainsItem != remainsEnd)
        {
            if(endItem.Value < remainsItem.Value)
            {
                break;
            }

            if(endItem.Value == remainsItem.Value)
            {
                resItem = AppendItem(resItem, endItem.Value);
                break;
            }

            remainsItem = remainsItem.Next;
        }

        if(remainsItem.Value == endItem.Value)
        {
            resItem = AppendItem(resItem, endItem.Value);
        }

        return resItem;
    }

    // Объединяет множества
    public Set Union(Set b)
    {
        if(b == null || b == this)
        {
            return new Set(b);
        }

        Item endItem = UnionList(b);
        Set newSet = new Set(Math.min(Start, b.Start), Math.max(End, b.End));
        newSet._end = endItem;

        return newSet;
    }

    // Объединяет множества
    private Item UnionList(Set b)
    {
        Item resItem = null;
        Item thisListIndex = _end.Next;
        Item bListIndex = b._end.Next;

        // Определяем первый эл-т
        if(thisListIndex.Value == bListIndex.Value)
        {
            resItem = new Item(thisListIndex.Value, null);
            bListIndex = bListIndex.Next;
            thisListIndex = thisListIndex.Next;
        }
        else if(thisListIndex.Value < bListIndex.Value)
        {
            resItem = new Item(thisListIndex.Value, null);
            thisListIndex = thisListIndex.Next;
        }
        else
        {
            resItem = new Item(bListIndex.Value, null);
            bListIndex = bListIndex.Next;
        }

        resItem.Next = resItem;
        int newItemValue;

        // Объединяем серединную часть
        while (thisListIndex != _end && bListIndex != b._end)
        {
            // Если ел-т меньше, то вставляем его и двигаем индекс
            if(thisListIndex.Value < bListIndex.Value)
            {
                newItemValue = thisListIndex.Value;
                thisListIndex = thisListIndex.Next;
            }
            else if(thisListIndex.Value > bListIndex.Value)
            {
                newItemValue = bListIndex.Value;
                bListIndex = bListIndex.Next;
            }
            // Если эл-ты равны, то вставляем один любой и двигаем два индекса
            else
            {
                newItemValue = thisListIndex.Value;
                thisListIndex = thisListIndex.Next;
                bListIndex = bListIndex.Next;
            }

            resItem = AppendItem(resItem, newItemValue);
        }

        Item remainsItem;
        Item remainsEnd;

        // Если закончилось оба списка
        if(thisListIndex == _end && bListIndex == b._end)
        {
            int firstValue = Math.min(thisListIndex.Value, bListIndex.Value);
            int secondValue = Math.max(thisListIndex.Value, bListIndex.Value);

            // Вставляем поочередно оба эл-та в зависимости от Value
            if(firstValue > resItem.Value)
            {
                resItem = AppendItem(resItem, firstValue);
            }

            if(secondValue > resItem.Value)
            {
                resItem = AppendItem(resItem, secondValue);
            }

            return resItem;
        }
        // Если закончился только один, то запоминаем какой эл-т и какой  список
        else if(thisListIndex == _end)
        {
            remainsItem = bListIndex;
            remainsEnd = b._end;
        }
        else
        {
            remainsItem = thisListIndex;
            remainsEnd = _end;
        }

        // Докидываем все эл-ты в список
        while (remainsItem != remainsEnd)
        {
            resItem = AppendItem(resItem, remainsItem.Value);
            remainsItem = remainsItem.Next;
        }

        return resItem;
    }

    // Вычитание множеств
    public Set Difference(Set b)
    {
        if(b == null)
        {
            return new Set(this);
        }

        if(b == this)
        {
            return null;
        }

        if(End <= b.Start || Start >= b.End)
        {
            return new Set(this);
        }

        Item endItem = DifferenceList(b);
        Set newSet = new Set(Start, End);
        newSet._end = endItem;

        return newSet;
    }

    // Вычитание множеств
    private Item DifferenceList(Set b)
    {
        // Копируем текущее множнство
        Set newSet = new Set(this);

        Item prevNewListIndex = null;
        Item newListIndex = newSet._end.Next;
        Item bListIndex = b._end.Next;


        while (newListIndex != newSet._end && bListIndex != b._end)
        {
            // Двигаем индексы во всех случаях кроме равенства значений
            // Если значения равны, то удаялем эл-т
            if(newListIndex.Value < bListIndex.Value)
            {
                prevNewListIndex = newListIndex;
                newListIndex = newListIndex.Next;
            }
            else if(newListIndex.Value > bListIndex.Value)
            {
                bListIndex = bListIndex.Next;
            }
            else
            {
                prevNewListIndex.Next = prevNewListIndex.Next.Next;
                newListIndex = prevNewListIndex.Next;
                bListIndex = bListIndex.Next;
            }
        }

        // Если закончилось множество b
        if(bListIndex == b._end)
        {
            return newSet._end;
        }

        // Если закончилось текущее множество, то ищем похожий эл-та в b
        Item bItem = b.GetFirstLessItem(newListIndex.Value);

        // Если не нашли похожий,то return
        if(bItem == null)
        {
            return newSet._end;
        }

        // Если нашли похожий, то удаляем последний эл-т
        if(bItem.Next.Value == newListIndex.Value)
        {
            Item temp = newListIndex.Next;
            newSet._end = prevNewListIndex;
            prevNewListIndex.Next = temp;
        }

        return newSet._end;
    }


    // Добавление эл-та в конец и возвращение конца
    private Item AppendItem(Item end, int value)
    {
        Item newItem = new Item(value, null);
        Item temp = end.Next;

        end.Next = newItem;
        newItem.Next = temp;

        return newItem;
    }

    // Находится ли значение в интервале множества
    private boolean IsInRange(int value)
    {
        return (value >= Start) && (value < End);
    }

    // Возвращает предыдущий эл-т за первым меньшим или равным некоторому значению
    private Item GetFirstLessItem(int value)
    {
        Item prevItem = _end;
        Item currentItem = _end.Next;

        while (currentItem != _end)
        {
            if(currentItem.Value >= value)
            {
                return prevItem;
            }

            prevItem = currentItem;
            currentItem = currentItem.Next;
        }

        return currentItem.Value >= value ? prevItem : null;
    }

    // Копирует одно множвество в другое
    private void CopySets(Set from, Set to)
    {
        to.Start = from.Start;
        to.End = from.End;

        // Копируем конец
        to._end = new Item(from._end);
        to._end.Next = _end;

        // Добавляем в конец с помощью функции AppendItem
        Item fromItem = from._end.Next;
        while (fromItem != from._end)
        {
            to._end = AppendItem(to._end, fromItem.Value);
            fromItem = fromItem.Next;
        }
    }
}

