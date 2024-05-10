import list.Set;

public class Main {
    public static void main(String[] args)
    {
        Set set1 = new Set(0, 32);
        set1.Insert(31);
        set1.Insert(4);
        set1.Print();

        Set set2 = new Set(0,120);
        set2.Insert(64);
        set2.Insert(4);
        set2.Print();

        set1.Intersection(set2).Print();
    }
}