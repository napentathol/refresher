# Refresher

## What does it do?

Refresher creates a guarantee that, when provided a factory and closing function, you will only ever have one of a given
resource.

## When should I use it?

You should use refresher if you have a resource that you only ever want one of, but you cannot manage this resource in a
standard try-with-resources block due to multi-threading or other issue.

## Is it thread-safe?

Sure hope so. Can't make any guarantees with user-supplied functions, though. Try to avoid side-effects in the
refreshing and closing functions.

## What assumptions does it make?

Not many. Refresher makes no assumptions about whether or not the contained resource is null. It only requires that the
refreshing supplier and closing consumer are not null.