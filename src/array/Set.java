package array;

public class Set
{
    public int Start; // Старт множества
    public int End; // Конец множества
    public int Length; // Длина массива

    private final int BITS_COUNT= 32; // Кол-во бит
    private int _offset; // Отступ относительно нуля
    private int[] _array;


    // Инициализирующий конструктор
    public Set(int start, int end)
    {
        // Вычисляем старт
        Start = RoundToBitesCount(Math.min(start, end), true);
        // Вычисляем конец
        End = RoundToBitesCount(Math.max(start, end), false);
        // Вычисляем отступ
        _offset = Start/BITS_COUNT;

        // Вычисляем длину массива и создаем его
        Length = Math.abs((End - Start) / BITS_COUNT);
        _array = new int[Length];
    }

    // Копирующий конструктор
    public Set(Set B)
    {
        CopySets(B, this);
    }

    // Объединение множеств
    public Set Union(Set b)
    {
        if(b == null || b == this)
        {
            return new Set(b);
        }

        // Вычисляем конец и старт
        int newMin = Math.min(Start, b.Start);
        int newMax = Math.max(End, b.End);

        // Создаем множество и вычисляем содержимое с помощью UnionArrays
        Set newSet = new Set(newMin, newMax);
        UnionArrays(b, newSet._array);

        return newSet;
    }

    // Пересечение множеств
    public Set Intersection(Set b)
    {
        if(b == null)
        {
            return null;
        }

        if(b == this)
        {
            return new Set(this);
        }

        // Если интервалы не пересекаются, то null
        if(End <= b.Start || Start >= b.End)
        {
            return null;
        }

        // Вычисляем конец и старт
        int newMin = Math.max(Start, b.Start);
        int newMax = Math.min(End, b.End);

        // Создаем множество и вычисляем содержимое с помощью IntersectArrays
        Set newSet = new Set(newMin, newMax);
        IntersectArrays(b, newSet._array);

        return newSet;
    }

    // Вычитание множества
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

        // Если интервалы не пересекаются, то вызываем копирующий конструктор
        if(End <= b.Start || Start >= b.End)
        {
            return new Set(this);
        }

        // Создаем множество и вычисляем содержимое с помощью DifferenceArrays
        Set newSet = new Set(Start, End);
        DifferenceArrays(b, newSet._array);

        return newSet;
    }

    // Объединение непересекающихся множнств множеств
    public Set Merge(Set b)
    {
        if(b == null || b == this)
        {
            return new Set(b);
        }

        int newMin = Math.min(Start, b.Start);
        int newMax = Math.max(End, b.End);

        // Создаем множество и вычисляем содержимое с помощью UnionArrays
        Set newSet = new Set(newMin, newMax);
        UnionArrays(b, newSet._array);

        return newSet;
    }

    // Находится ли число в множестве
    public boolean Member(int x)
    {
        // Получем индекс эл-та в массиве
        int intIndex = GetArrayElementIndexByValue(x);
        // Получаем бит, в котором может находится число
        int indexInInt = GetIndexInInt(x);
        // Получаем битовую маску
        int bitMask = GenerateBitMask(indexInInt);

        // Проверяем наличие с помощью &
        return (_array[intIndex] & bitMask) > 0;
    }

    // Вставляет число в множество
    public void Insert(int x)
    {
        // Получем индекс эл-та в массиве
        int intIndex = GetArrayElementIndexByValue(x);
        // Получаем бит, в котором может находится число
        int indexInInt = GetIndexInInt(x);
        // Получаем битовую маску
        int bitMask = GenerateBitMask(indexInInt);

        // Производим операцию | с эл-том массива и маской
        _array[intIndex] |= bitMask;
    }

    // Удаляет число из множество
    public void Delete(int x)
    {
        // Получем индекс эл-та в массиве
        int intIndex = GetArrayElementIndexByValue(x);
        // Получаем бит, в котором может находится число
        int indexInInt = GetIndexInInt(x);
        // Получаем битовую маску
        int bitMask = GenerateBitMask(indexInInt);

        _array[intIndex] &= ~(bitMask);
    }

    // Возвращает минимальное значение в множестве
    public int Min()
    {
        for(int i = 0; i < _array.length; i++)
        {
            if(_array[i] == 0)
            {
                continue;
            }

            for(int j = 0; j < BITS_COUNT; j++)
            {
                if((_array[i] & GenerateBitMask(j)) > 0)
                {
                    return GetNumberFromIndexes(i, j);
                }
            }
        }

        throw new RuntimeException("Empty set");
    }

    // Возвращает максимальное значение в множестве
    public int Max()
    {
        for(int i = _array.length - 1; i >= 0; i--)
        {
            if(_array[i] == 0)
            {
                continue;
            }

            for(int j = BITS_COUNT-1; j >= 0; j--)
            {
                if((_array[i] & GenerateBitMask(j)) > 0)
                {
                    return GetNumberFromIndexes(i, j);
                }
            }
        }

        throw new RuntimeException("Empty set");
    }

    // Вывод множества на экран
    public void Print()
    {
        var start = Start;
        var end = Start + BITS_COUNT -1;
        for(int i = 0; i < _array.length; i++)
        {
            System.out.format("%3d. (%4d/%4d): %s\n", i, start, end, GetStringBinary(_array[i]));
            start = end + 1;
            end += BITS_COUNT;
        }
    }

    // Обнуление множества
    public void MakeNull()
    {
        _array = null;
    }


    // Копирование множества
    public void Assign(Set b)
    {
        CopySets(b, this);
    }


    // Проверка на равенство множеств
    public boolean Equal(Set b)
    {
        // Определяем, какое множество начинается раньше
        Set startSet;

        if(Start < b.Start)
        {
            startSet = this;
        }
        else
        {
            startSet = b;
        }

        int currentMin = startSet.Start;


        for(int i = 0, bI = 0; i < startSet._array.length; i++)
        {
            // Если this значения уже начились, а во множестве b еще нет
            if(_array[i] != 0 && !b.IsInRange(currentMin))
            {
                return false;
            }

            // Сравниваем значения
            if(b.IsInRange(currentMin) && _array[i] != b._array[bI++])
            {
                return false;
            }

            // Увеличиваем проверяемое значение
            currentMin += BITS_COUNT;
        }

        return true;
    }

    public boolean IsEmpty()
    {
        if(_array == null)
        {
            return true;
        }

        return IsEmptyArray(_array);
    }

    // Возвращает множество где находится эл-т
    public Set Find(Set b, int x)
    {
        // Проверяем нахождение в текущем множестве
        if(IsInRange(x))
        {
            int intIndex = GetArrayElementIndexByValue(x);
            int indexInInt = GetIndexInInt(x);
            int bitMask = GenerateBitMask(indexInInt);

            if((_array[intIndex] & bitMask) > 0)
            {
                return this;
            }
        }

        // Проверяем нахождение в множестве b множестве
        if(b.IsInRange(x))
        {
            int bIntIndex = b.GetArrayElementIndexByValue(x);
            int bIndexInInt = b.GetIndexInInt(x);
            int bBitMask = b.GenerateBitMask(bIndexInInt);

            if((b._array[bIntIndex] & bBitMask) > 0)
            {
                return b;
            }
        }

        return null;
    }

    // Провеяем, находится ли значение в пределах Start и End
    public boolean IsInRange(int value)
    {
        return (value >= Start) && (value < End);
    }


    // Проверяем пересекаются ли множества
    public boolean IsIntersectWith(Set b)
    {
        // Создаем массив, в котором будет лежать результат
        int newMin = Math.min(Start, b.Start);
        int newMax = Math.max(End, b.End);
        int[] resArray = new int[Math.abs((newMax - newMin) / BITS_COUNT)];

        // Пересекаем массивы
        IntersectArrays(b, resArray);

        // Пустой ли получивщийся массив?
        return !IsEmptyArray(resArray);
    }

    // Проверяем пустой ли массив
    private boolean IsEmptyArray(int[] array)
    {
        for(int i = 0; i < array.length; i++)
        {
            if(array[i] != 0)
            {
                return false;
            }
        }

        return true;
    }

    // Получаем битовую маску по индексу в int
    private int GenerateBitMask(int indexInInt)
    {
        return 1 << (BITS_COUNT - indexInInt - 1);
    }

    // Получаем число по индексу в массиве и в int
    private int GetNumberFromIndexes(int intIndex, int indexInInt)
    {
        return Start * (intIndex + 1) + indexInInt;
    }

    // Получаем по числу индекс в int
    private int GetIndexInInt(int x)
    {
        return x % BITS_COUNT;
    }

    // Возвращает число в бинарном представлении
    private String GetStringBinary(int num)
    {
        StringBuilder binary = new StringBuilder();
        for(int i = 0; i < 32; i++)
        {
            int remainder = Math.abs(num % 2);
            binary.insert(0, remainder);
            num /= 2;
        }

        return binary.toString();
    }

    // Округляет число кратного 32
    private int RoundToBitesCount(int x, boolean isStart)
    {
        if(x % BITS_COUNT == 0)
        {
            return x;
        }

        if(isStart)
        {
            return (int)Math.floor((x + 0.f) / BITS_COUNT) * BITS_COUNT;
        }

        return (int)Math.ceil((x + 0.f) / BITS_COUNT) * BITS_COUNT;
    }

    // Копирует массивы
    private void CopyArrays(int[] fromArray, int[] toArray)
    {
        for(int i = 0; i < fromArray.length; i++)
        {
            toArray[i] = fromArray[i];
        }
    }

    // Метод копирует одно множество в другое
    private void CopySets(Set fromSet, Set toSet)
    {
        toSet.Start = fromSet.Start;
        toSet.End = fromSet.End;
        toSet._offset = fromSet._offset;
        toSet._array = new int[fromSet._array.length];

        CopyArrays(fromSet._array, toSet._array);
    }

    // Получаем индекс в массиве по значению
    private int GetArrayElementIndexByValue(int value)
    {
        int wholeStart = Math.round(value / BITS_COUNT);;

        if(value < 0 && value % 32 != 0)
        {
            wholeStart -= 1;
        }

        return wholeStart - _offset;
    }

    // Пересекает массивы
    public void IntersectArrays(Set b, int[] resArray)
    {
        // Определяем, какое множество идет первым
        Set lowerSet = Start > b.Start ? b : this;
        Set higherSet = Start > b.Start ? this : b;

        int currentSum = lowerSet.Start;

        int lowerSetIndex = 0;
        int higherSetIndex = 0;
        int resIndex = 0;

        while (lowerSetIndex < lowerSet.Length)
        {
            // Если текущее currentSum есть в обоих массивах, значит помещаем новое значение
            if(lowerSet.IsInRange(currentSum) && higherSet.IsInRange(currentSum))
            {
                resArray[resIndex++] = lowerSet._array[lowerSetIndex++] & higherSet._array[higherSetIndex++];
            }

            // Если текущее currentSum есть только в lowerSet, то двигаем его
            else if(lowerSet.IsInRange(currentSum))
            {
                lowerSetIndex++;
            }

            // Увеличиваем проверяемое значение
            currentSum += BITS_COUNT;
        }
    }

    // Объединяет массивы
    public void UnionArrays(Set b, int[] resArray)
    {
        // Определяем, какое множество идет первым
        Set lowerSet = Start > b.Start ? b : this;
        Set higherSet = Start > b.Start ? this : b;

        int currentSum = lowerSet.Start;

        int lowerSetIndex = 0;
        int higherSetIndex = 0;
        int resIndex = 0;

        while (lowerSetIndex < lowerSet.Length || higherSetIndex < higherSet.Length)
        {
            // Если текущее currentSum есть в обоих массивах, значит помещаем новое значение
            if(lowerSet.IsInRange(currentSum) && higherSet.IsInRange(currentSum))
            {
                resArray[resIndex++] = lowerSet._array[lowerSetIndex++] | higherSet._array[higherSetIndex++];
            }
            // Если только в одном то помещям это значение
            else if(lowerSet.IsInRange(currentSum))
            {
                resArray[resIndex++] = lowerSet._array[lowerSetIndex++];
            }
            else if (higherSet.IsInRange(currentSum))
            {
                resArray[resIndex++] = higherSet._array[higherSetIndex++];
            }

            // Увеличиваем проверяемое значение
            currentSum += BITS_COUNT;
        }
    }

    // Вычитание массива
    public void DifferenceArrays(Set b, int[] resArray)
    {
        int currentSum = Math.min(b.Start, Start);

        int thisIndex = 0;
        int bIndex = 0;
        int resIndex = 0;

        while (thisIndex < Length && bIndex < b.Length)
        {
            // Если текущее currentSum есть в обоих массивах, значит помещаем новое значение
            if(IsInRange(currentSum) && b.IsInRange(currentSum))
            {
                resArray[resIndex++] = _array[thisIndex] ^ (_array[thisIndex] & b._array[bIndex++]);
                thisIndex++;
            }
            // Сдвигаем массивы до соединения
            else if(!IsInRange(currentSum))
            {
                bIndex++;
            }
            else if (!b.IsInRange(currentSum))
            {
                resArray[resIndex++] = _array[thisIndex++];
            }

            // Увеличиваем проверяемое значение
            currentSum += BITS_COUNT;
        }

        // Если не дошли до конца текущего массива, то просто заносим все оставшиеся значения
        if (thisIndex == Length)
        {
            return;
        }

        while (thisIndex < Length)
        {
            resArray[resIndex++] = _array[thisIndex++];
        }
    }
}

