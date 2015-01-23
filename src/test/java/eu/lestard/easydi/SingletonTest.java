package eu.lestard.easydi;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonTest {
	
	
	private EasyDI easyDI;
	
	@Before
	public void setup() {
		easyDI = new EasyDI();
	}
	
	
	
	@Test
	public void success_nonSingleton_newInstanceEverytime() {
		class Example {
			public Example() {
			}
		}
		
		final Example instanceOne = easyDI.getInstance(Example.class);
		final Example instanceTwo = easyDI.getInstance(Example.class);
		
		assertThat(instanceOne).isNotNull().isNotSameAs(instanceTwo);
	}
	
	@Test
	public void success_nonSingleton_biggerExample() {
		class Dependency {
			public Dependency() {
			}
		}
		
		class Other {
			final Dependency dep;
			
			public Other(Dependency dep) {
				this.dep = dep;
			}
		}
		
		class Example {
			public final Dependency dep;
			public final Other other;
			
			public Example(Dependency dep, Other other) {
				this.dep = dep;
				this.other = other;
			}
		}
		
		final Example instance = easyDI.getInstance(Example.class);
		
		assertThat(instance.dep).isNotSameAs(instance.other.dep);
	}
	
	@Test
	public void success_singleton() {
		@Singleton
		class Example {
			public Example() {
			}
		}
		
		final Example instanceOne = easyDI.getInstance(Example.class);
		final Example instanceTwo = easyDI.getInstance(Example.class);
		
		assertThat(instanceOne).isSameAs(instanceTwo);
	}
	
	@Test
	public void success_singleton_biggerExample() {
		@Singleton
		class Dependency {
			public Dependency() {
			}
		}
		
		class Other {
			final Dependency dep;
			
			public Other(Dependency dep) {
				this.dep = dep;
			}
		}
		
		class Example {
			public final Dependency dep;
			public final Other other;
			
			public Example(Dependency dep, Other other) {
				this.dep = dep;
				this.other = other;
			}
		}
		
		final Dependency dependency = easyDI.getInstance(Dependency.class);
		final Example instance = easyDI.getInstance(Example.class);
		
		assertThat(dependency).isSameAs(instance.dep).isSameAs(instance.other.dep);
	}
	
	@Test
	public void success_singleton_noAnnotationButMarkedPerMethod() {
		class Example {
			public Example() {
			}
		}
		
		easyDI.markAsSingleton(Example.class);
		
		final Example instanceOne = easyDI.getInstance(Example.class);
		final Example instanceTwo = easyDI.getInstance(Example.class);
		
		assertThat(instanceOne).isNotNull().isSameAs(instanceTwo);
	}
	
	public static interface MyInterface {
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fail_markAsSingleton_paramIsAnInterface() {
		easyDI.markAsSingleton(ProviderTest.MyInterface.class);
	}
	
}
