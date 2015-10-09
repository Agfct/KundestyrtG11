package modules;

import java.io.File;
import java.util.ArrayList;

public class TestClass {
	public static void main(String[] args) {
		
		ArrayList<TimelineMediaObject> tlmo_list = new ArrayList<TimelineMediaObject>();
		MediaObject parent = new MediaObject("some/path", "Test parent 1", MediaSourceType.VIDEO);
		TimelineMediaObject tlmo = new TimelineMediaObject(0, 1, 1, parent);
		tlmo_list.add(tlmo);
		tlmo = new TimelineMediaObject(0, 2, 1, parent);
		tlmo_list.add(tlmo);
		tlmo = new TimelineMediaObject(0, 3, 2, parent);
		tlmo_list.add(tlmo);

		
		StorageController sc = StorageController.getInstance();
		System.out.println(sc.storeObject(tlmo_list, new File("test.data")));
		
		ArrayList<TimelineMediaObject> loaded_list = (ArrayList<TimelineMediaObject>) sc.loadObject(new File("test.data"));
		
		System.out.println("debug_point");
		
		System.out.println("Finished");
	}
}