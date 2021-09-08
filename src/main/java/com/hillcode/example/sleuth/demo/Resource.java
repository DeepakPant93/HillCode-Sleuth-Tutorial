package com.hillcode.example.sleuth.demo;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.propagation.TraceIdContext;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sleuth-demo")
@Slf4j
public class Resource {

	@Autowired
	private Tracer tracer;

	@GetMapping(value = "/default-trace/{id}")
	public String defaultTrace(@PathVariable("id") String id) {
		log.info("Hello - {}", id);
		return "Done";
	}

	@GetMapping(value = "/new-trace/{id}")
	public String newTrace(@PathVariable("id") String id) {
		log.info("Hi - {}", id);

		Span span = this.tracer
				.nextSpan(TraceContextOrSamplingFlags
						.newBuilder(TraceIdContext.newBuilder().traceId(Long.valueOf(id)).build()).build())
				.name("user-id").start();

		tracer.withSpanInScope(span.start());

		log.info("This is final Hi - {}", id);
		return "Done";
	}

	@GetMapping(value = "/new-span/{id}")
	public String newSpan(@PathVariable("id") String id) {
		log.info("Hi - {}", id);

		Span span = this.tracer.nextSpan(TraceContextOrSamplingFlags.newBuilder(TraceContext.newBuilder()
				.traceIdHigh(Long.MAX_VALUE).traceId(Long.valueOf(id)).spanId(Long.valueOf(id)).build()).build())
				.name("user-id").start();
		tracer.withSpanInScope(span.start());

		log.info("This is final Hi - {}", id);
		return "Done";
	}

	@GetMapping(value = "/new-id/{id}")
	public String newId(@PathVariable("id") String id) {

		// MDC is responsible to print the userId in logs
		MDC.put("userId", id);
		log.info("This is final Hi - {}", id);
		return "Done";
	}
}
