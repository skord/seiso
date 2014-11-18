/* 
 * Copyright 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.seiso.web.controller;


/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class MachineControllerTests {

    // TODO test still needed?
    
//	// Class under test
//	@InjectMocks private MachineController controller;
//	
//	// Dependencies
//	@Mock private MachineRepo machineRepo;
//	
//	// Test data
//	@Mock private MachineSearch search;
//	@Mock private BindingResult bindingResult;
//	@Mock private Page<Machine> machinePage;
//	
//	@Before
//	public void init() throws Exception {
//		this.controller = new MachineController();
//		MockitoAnnotations.initMocks(this);
//		when(machineRepo.search(eq(search), (Pageable) anyObject())).thenReturn(machinePage);
//	}
//	
//	@Test
//	public void search() {
//		val pageable = new PageRequest(1, 100);
//		val result = controller.search(search, bindingResult, pageable);
//		assertNotNull(result);
//		verify(machineRepo).search((MachineSearch) anyObject(), eq(pageable));
//	}
//	
//	@Test
//	public void search_invalidRequest() {
//		when(bindingResult.hasErrors()).thenReturn(true);
//		val pageable = new PageRequest(1, 100);
//		
//		try {
//			controller.search(search, bindingResult, pageable);
//			fail();
//		} catch (InvalidRequestException e) {
//			assertSame(bindingResult, e.getBindingResult());
//		}
//	}
}
